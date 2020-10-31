package main

import org.scalatest.{Matchers, WordSpec}

class ProductsRatingsMainSpec extends WordSpec with Matchers {
  "The ProductsRatingsMainSpec" when {
    "calling calculate on a valid csv file path" should {
      "return the products ratings summary in JSON format" in {
        val productsRatingsSummaryAsJson = ProductsRatingsMain.calculate("src/test/resources/validRatings.csv")
        productsRatingsSummaryAsJson shouldBe
          """{
            |  "validLines" : 4,
            |  "invalidLines" : 0,
            |  "bestRatedProducts" : [
            |    "lights-02",
            |    "lights-01",
            |    "chain-01"
            |  ],
            |  "worstRatedProducts" : [
            |    "saddle-01",
            |    "chain-01",
            |    "lights-01"
            |  ],
            |  "mostRatedProducts" : [
            |    "lights-02",
            |    "saddle-01",
            |    "chain-01"
            |  ],
            |  "lessRatedProducts" : [
            |    "lights-01",
            |    "chain-01",
            |    "saddle-01"
            |  ]
            |}""".stripMargin

      }
    }
  }

  "calling calculate on an invalid csv file path" should {
    "return an error message" in {
      val productsRatingsSummaryAsJson = ProductsRatingsMain.calculate("/invalid.csv")
      productsRatingsSummaryAsJson shouldBe
        """We could not calculate a products ratings summary for the given csv file, an error occurred,details: FileNotFoundException: /invalid.csv (No such file or directory)""".stripMargin

    }
  }
}
