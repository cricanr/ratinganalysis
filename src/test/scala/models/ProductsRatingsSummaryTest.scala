package models

import org.scalatest.{Matchers, WordSpec}

class ProductsRatingsSummaryTest extends WordSpec with Matchers {
  "The ProductsRatingsSummary" when {
    "calling getProductsRatingsSummaryAsJson on a ProductsRatingsSummary" should {
      "return the JSON for the object" in {
        val productsRatingsSummary = ProductsRatingsSummary(11, 2,
          Seq("volvo", "toyota", "mazda"),
          Seq("lada", "dacia", "vw"),
          Seq("opel", "fiat", "citroen"),
          Seq("lada", "trabant", "skoda"))

        ProductsRatingsSummary.getProductsRatingsSummaryAsJson(productsRatingsSummary) shouldBe
          """{
            |  "validLines" : 11,
            |  "invalidLines" : 2,
            |  "bestRatedProducts" : [
            |    "volvo",
            |    "toyota",
            |    "mazda"
            |  ],
            |  "worstRatedProducts" : [
            |    "lada",
            |    "dacia",
            |    "vw"
            |  ],
            |  "mostRatedProducts" : [
            |    "opel",
            |    "fiat",
            |    "citroen"
            |  ],
            |  "lessRatedProducts" : [
            |    "lada",
            |    "trabant",
            |    "skoda"
            |  ]
            |}""".stripMargin
      }
    }
  }
}