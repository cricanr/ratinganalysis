package services

import com.google.inject.Inject
import csvparser.{IProductParser, ProductCSVParser}
import models.{Product, ProductValidator, ProductsRatingsSummary}

trait IProductsService {
  def getProducts(productsRaw: List[List[String]]): Seq[Either[Object, Product]]

  def getProductsRatingsSummary: Either[Throwable, ProductsRatingsSummary]
}

class ProductsService @Inject()(productParser: IProductParser) extends IProductsService {
  override def getProductsRatingsSummary: Either[Throwable, ProductsRatingsSummary] = {
    val eitherProductsRaw = productParser.readAllProducts()
    eitherProductsRaw.map { productsRaw =>
      val eitherProducts = getProducts(productsRaw)
      val validProducts: Seq[Product] = eitherProducts.collect { case Right(product) => product }

      val countValidProducts = validProducts.size
      val countInvalidProducts = eitherProducts.count(product => product.isLeft)

      val productsByProductId = validProducts.groupBy(product => product.productId)
      val productsWithAvgRating = productsByProductId.map {
        case (key, value) => key -> value.foldLeft(0)(_ + _.rating) / value.size
      }
      val productsWithAvgRatingSortedDesc = productsWithAvgRating.toSeq.sorted

      val bestRatedProducts = productsWithAvgRatingSortedDesc.takeRight(3).map(_._1)
      val worstRatedProducts = productsWithAvgRatingSortedDesc.take(3).map(_._1)

      val productsWithRatingCount = productsByProductId.map { case (key, value) =>
        key -> value.size
      }

      val productsWithSortedRatingCount = productsWithRatingCount.toSeq.sorted

      val mostRatedProducts = productsWithSortedRatingCount.takeRight(3).map(_._1)
      val lessRatedProducts = productsWithSortedRatingCount.take(3).map(_._1)

      val productsRatingsSummary = ProductsRatingsSummary(countValidProducts,
        countInvalidProducts,
        bestRatedProducts,
        worstRatedProducts,
        mostRatedProducts,
        lessRatedProducts)

      productsRatingsSummary
    }
  }

  def getProducts(productsRaw: List[List[String]]): Seq[Either[Object, Product]] = {
    productsRaw.map { productLine =>
      val productEither = ProductCSVParser.getProductFromCsvLine(productLine)
      productEither.flatMap(product => ProductValidator.validateProduct(product.buyerId, product.shopId, product.productId, product.rating))
    }
  }
}
