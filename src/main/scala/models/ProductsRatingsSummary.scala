package models

import io.circe.generic.auto._
import io.circe.syntax.EncoderOps

case class ProductsRatingsSummary(validLines: Int,
                                  invalidLines: Int,
                                  bestRatedProducts: Seq[String],
                                  worstRatedProducts: Seq[String],
                                  mostRatedProduct: String,
                                  lessRatedProduct: String)

object ProductsRatingsSummary {
  def getProductsRatingsSummaryAsJson(productsRatingsSummary: ProductsRatingsSummary): String = {
    productsRatingsSummary.asJson.toString()
  }
}