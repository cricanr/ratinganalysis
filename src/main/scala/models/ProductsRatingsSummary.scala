package models

case class ProductsRatingsSummary(validLines: Int,
                                  invalidLines: Int,
                                  bestRatedProducts: Seq[String],
                                  worstRatedProducts: Seq[String],
                                  mostRatedProducts: Seq[String],
                                  lessRatedProducts: Seq[String])