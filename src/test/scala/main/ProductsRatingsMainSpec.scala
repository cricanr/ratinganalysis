package main

import org.scalatest._

class ProductsRatingsMainSpec extends FlatSpec with Matchers {
  "The Hello object" should "say hello" in {
    ProductsRatingsMain.greeting shouldEqual "hello"
  }
}
