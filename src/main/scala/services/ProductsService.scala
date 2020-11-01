package services

import com.google.inject.Inject
import csvparser.{IProductParser, ProductCSVParser}
import models.{Product, ProductValidator, ProductsRatingsSummary}

trait IProductsService {
  def getProducts(productsRaw: List[List[String]]): Seq[Either[Object, Product]]

  def getProductsRatingsSummary(
      csvFilePath: String
  ): Either[Throwable, ProductsRatingsSummary]
}

class ProductsService @Inject() (productParser: IProductParser)
    extends IProductsService {

  override def getProductsRatingsSummary(
      csvFilePath: String
  ): Either[Throwable, ProductsRatingsSummary] = {
    val eitherProductsRaw = productParser.readAllProducts(csvFilePath)
    eitherProductsRaw.map { productsRaw =>
      val eitherProducts = getProducts(productsRaw)
      ProductsService.getProductRatingSummary(eitherProducts)
    }
  }

  def getProducts(
      productsRaw: List[List[String]]
  ): Seq[Either[Object, Product]] = {
    productsRaw.map { productLine =>
      val productEither = ProductCSVParser.getProductFromCsvLine(productLine)
      productEither.flatMap(product =>
        ProductValidator.validateProduct(
          product.buyerId,
          product.shopId,
          product.productId,
          product.rating
        )
      )
    }
  }
}

object ProductsService {
  def getProductRatingSummary(
      eitherProducts: Seq[Either[Object, Product]]
  ): ProductsRatingsSummary = {
    val validProducts: Seq[Product] = eitherProducts.collect {
      case Right(product) => product
    }

    val countValidProducts = validProducts.size
    val countInvalidProducts = eitherProducts.count(product => product.isLeft)

    val productsByProductId =
      validProducts.groupBy(product => product.productId)
    val productsWithAvgRating = productsByProductId.map { case (key, value) =>
      key -> value.foldLeft(0)(_ + _.rating) / value.size
    }
    val productsWithAvgRatingSorted = productsWithAvgRating.toSeq.sortBy {
      case (_, ratingAvg) => ratingAvg
    }

    val bestRatedProducts =
      productsWithAvgRatingSorted.takeRight(3).reverseIterator.map(_._1).toSeq
    val worstRatedProducts = productsWithAvgRatingSorted.take(3).map(_._1)

    val productsWithRatingCount = productsByProductId.map { case (key, value) =>
      key -> value.size
    }

    val productsWithSortedRatingCount =
      productsWithRatingCount.toSeq.sortBy(_._2)

    val mostRatedProducts = productsWithSortedRatingCount
      .takeRight(1)
      .map(_._1)
      .headOption
      .getOrElse("")
    val lessRatedProducts =
      productsWithSortedRatingCount.headOption.map(_._1).getOrElse("")

    val productsRatingsSummary = ProductsRatingsSummary(
      countValidProducts,
      countInvalidProducts,
      bestRatedProducts,
      worstRatedProducts,
      mostRatedProducts,
      lessRatedProducts
    )

    productsRatingsSummary
  }
}
