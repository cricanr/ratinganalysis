package services

import models.{Product, ProductValidator, ProductsRatingsSummary}

trait IProductsService {
  def getProductsRatingsSummary(productsRaw: List[List[String]]): ProductsRatingsSummary
}

class ProductsService extends IProductsService {
  override def getProductsRatingsSummary(productsRaw: List[List[String]]): ProductsRatingsSummary = {
    val eitherProducts = productsRaw.map { productLine =>
      val product = Product(productLine.head, productLine(1), productLine(2), productLine(3).toInt)
      ProductValidator.validateProduct(product.buyerId, product.shopId, product.productId, product.rating)
    }
    val validProducts = eitherProducts.filter(eitherProduct => eitherProduct.isRight).map(product => product.getOrElse(Product("", "", "", 0)))

    val countValidProducts = validProducts.size
    val countInvalidProducts = eitherProducts.count(product => product.isLeft)

    val productsByProductId = validProducts.groupBy(product => product.productId)
    val productsWithAvgRating = productsByProductId.map { case (key, value) => key -> value.foldLeft(0)(_+_.rating) /value.size }
    val productsWithAvgRatingSortedDesc = productsWithAvgRating.toSeq.sorted

    val bestRatedProducts = productsWithAvgRatingSortedDesc.takeRight(3).map(_._1)
    val worstRatedProducts = productsWithAvgRatingSortedDesc.take(3).map(_._1)

    val productsWithRatingCount = productsByProductId.map { case (key, value) =>
      key -> value.size}

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
