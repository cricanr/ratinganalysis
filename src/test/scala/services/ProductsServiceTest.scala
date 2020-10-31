package services

import java.io.FileNotFoundException

import csvparser.IProductParser
import models._
import org.mockito.Mockito.when
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar

class ProductsServiceTest extends WordSpec with MockitoSugar with Matchers {
  "The Products Service" when {
    "calling getProducts on valid raw products that have valid format" should {
      "return the Products" in {
        val productsParserMock = mock[IProductParser]
        val productsRaw = List(
          List("buyer1", "veloshop", "chain-01", "4"),
          List("buyer2", "veloshop", "chain-02", "2"),
          List("buyer3", "veloshop3", "chain-04", "3")
        )
        when(productsParserMock.readAllProducts("/somePath")).thenReturn(Right(productsRaw))
        val productsService = new ProductsService(productsParserMock)

        productsService.getProducts(productsRaw) shouldBe List(
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer2", "veloshop", "chain-02", 2)),
          Right(Product("buyer3", "veloshop3", "chain-04", 3)),
        )
      }
    }

    "calling getProducts on valid empty raw products that have valid format" should {
      "return no Product" in {
        val productsParserMock = mock[IProductParser]
        val productsRaw = List.empty

        when(productsParserMock.readAllProducts("/somePath")).thenReturn(Right(productsRaw))
        val productsService = new ProductsService(productsParserMock)

        productsService.getProducts(productsRaw) shouldBe List.empty
      }
    }

    "calling getProducts on invalid raw products that have valid format" should {
      "return the failure for that product line with failure and the rest of the valid products" in {
        val productsParserMock = mock[IProductParser]
        val productsRaw = List(
          List("veloshop", "chain-01", "4"),
          List("buyer2", "veloshop", "chain-02", "2"),
          List("buyer3", "veloshop3", "chain-04", "3")
        )
        when(productsParserMock.readAllProducts("/somePath")).thenReturn(Right(productsRaw))
        val productsService = new ProductsService(productsParserMock)

        val productsEither = productsService.getProducts(productsRaw)
        productsEither containsSlice Seq(
          Right(Product("buyer2", "veloshop", "chain-02", 2)),
          Right(Product("buyer3", "veloshop3", "chain-04", 3))
        )

        productsEither.collect { case Left(failure: IndexOutOfBoundsException) => failure }.size shouldBe 1
      }
    }

    "calling getProducts on valid raw products that have invalid format" should {
      "return the invalid format errors" in {
        val productsParserMock = mock[IProductParser]
        val productsRaw = List(
          List("1buyer1", "veloshop", "chain-01", "4"),
          List("buyer2", "veloshop", "chain-100", "2"),
          List("buyer3", "1veloshop3", "chain-04", "3"),
          List("buyer4", "veloshop4", "chain-04", "10"),
          List("1buyer5", "2veloshop4", "3chain-04", "109")
        )
        when(productsParserMock.readAllProducts("/somePath")).thenReturn(Right(productsRaw))
        val productsService = new ProductsService(productsParserMock)

        val productsEither = productsService.getProducts(productsRaw)
        productsEither containsSlice Seq(
          Left(BuyerIdInvalid),
          Left(ProductIdInvalid),
          Left(ShopIdInvalid),
          Left(RatingInvalid),
          Left(BuyerIdInvalid, ProductIdInvalid, ShopIdInvalid, RatingInvalid)
        )
      }
    }

    "calling getProductRatingSummary on valid input" should {
      "return the summary" in {
        val summary = ProductsService.getProductRatingSummary(Seq(
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer2", "veloshop", "chain-02", 2)),
          Right(Product("buyer3", "veloshop3", "chain-04", 5)),
          Right(Product("buyer4", "veloshop", "chain-03", 1)),
        ))

        summary.validLines shouldBe 5
        summary.invalidLines shouldBe 0
        summary.bestRatedProducts shouldBe List("chain-04", "chain-01", "chain-02")
        summary.worstRatedProducts shouldBe List("chain-03", "chain-02", "chain-01")
        summary.mostRatedProducts shouldBe List("chain-01", "chain-02", "chain-04")
        summary.lessRatedProducts shouldBe List("chain-03", "chain-04", "chain-02")
      }
    }

    "calling getProductRatingSummary on valid input with just 3 products" should {
      "return the summary" in {
        val summary = ProductsService.getProductRatingSummary(Seq(
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer2", "veloshop", "chain-02", 2)),
          Right(Product("buyer4", "veloshop", "chain-03", 1))
        ))

        summary.validLines shouldBe 3
        summary.invalidLines shouldBe 0
        summary.bestRatedProducts shouldBe List("chain-01", "chain-02", "chain-03")
        summary.worstRatedProducts shouldBe List("chain-03", "chain-02", "chain-01")
        summary.mostRatedProducts shouldBe List("chain-02", "chain-01", "chain-03")
        summary.lessRatedProducts shouldBe List("chain-03", "chain-01", "chain-02")
      }
    }

    "calling getProductRatingSummary on valid input with just 2 products, 1 with 2 ratings" should {
      "return the summary" in {
        val summary = ProductsService.getProductRatingSummary(Seq(
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer2", "veloshop", "chain-01", 2)),
          Right(Product("buyer4", "veloshop", "chain-03", 1))
        ))

        summary.validLines shouldBe 3
        summary.invalidLines shouldBe 0
        summary.bestRatedProducts shouldBe List("chain-01", "chain-03")
        summary.worstRatedProducts shouldBe List("chain-03", "chain-01")
        summary.mostRatedProducts shouldBe List("chain-01", "chain-03")
        summary.lessRatedProducts shouldBe List("chain-03", "chain-01")
      }
    }

    "calling getProductRatingSummary on valid input with no products" should {
      "return the summary" in {
        val summary = ProductsService.getProductRatingSummary(Seq.empty)

        summary.validLines shouldBe 0
        summary.invalidLines shouldBe 0
        summary.bestRatedProducts shouldBe List.empty
        summary.worstRatedProducts shouldBe List.empty
        summary.mostRatedProducts shouldBe List.empty
        summary.lessRatedProducts shouldBe List.empty
      }
    }

    "calling getProductRatingSummary on valid input with 1 product" should {
      "return the summary" in {
        val summary = ProductsService.getProductRatingSummary(Seq(
          Right(Product("buyer1", "veloshop", "chain-01", 4))
        ))

        summary.validLines shouldBe 1
        summary.invalidLines shouldBe 0
        summary.bestRatedProducts shouldBe List("chain-01")
        summary.worstRatedProducts shouldBe List("chain-01")
        summary.mostRatedProducts shouldBe List("chain-01")
        summary.lessRatedProducts shouldBe List("chain-01")
      }
    }

    "calling getProductRatingSummary on invalid input" should {
      "return the summary without considering the invalid product" in {
        val summary = ProductsService.getProductRatingSummary(Seq(
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer2", "veloshop", "chain-02", 2)),
          Left(Product("1buyer3", "veloshop3", "chain-04", 5)),
          Right(Product("buyer4", "veloshop", "chain-03", 1)),
        ))

        summary.validLines shouldBe 4
        summary.invalidLines shouldBe 1
        summary.bestRatedProducts shouldBe List("chain-01", "chain-02", "chain-03")
        summary.worstRatedProducts shouldBe List("chain-03", "chain-02", "chain-01")
        summary.mostRatedProducts shouldBe List("chain-01", "chain-02", "chain-03")
        summary.lessRatedProducts shouldBe List("chain-03", "chain-02", "chain-01")
      }
    }

    "calling getProductRatingSummary on just invalid input" should {
      "return the empty summary" in {
        val summary = ProductsService.getProductRatingSummary(Seq(
          Left(Product("buyer1", "veloshop", "chain-01", 4)),
          Left(Product("buyer1", "veloshop", "chain-01", 4)),
          Left(Product("buyer2", "veloshop", "chain-02", 2)),
          Left(Product("1buyer3", "veloshop3", "chain-04", 5)),
          Left(Product("buyer4", "veloshop", "chain-03", 1)),
        ))

        summary.validLines shouldBe 0
        summary.invalidLines shouldBe 5
        summary.bestRatedProducts shouldBe List.empty
        summary.worstRatedProducts shouldBe List.empty
        summary.mostRatedProducts shouldBe List.empty
        summary.lessRatedProducts shouldBe List.empty
      }
    }

    "calling getProductRatingSummary on a large set of data" should {
      "return the summary" in {
        val eitherProducts = Seq(
          Right(Product("buyer1", "veloshop", "chain-01", 4)),
          Right(Product("buyer1", "veloshop", "lights-02", 5)),
          Right(Product("buyer1", "veloshop", "lights-01", 5)),
          Right(Product("buyer1", "veloshop", "saddle-01", 3)),
          Right(Product("buyer1", "tshirtfactory", "endura-01", 2)),
          Right(Product("buyer1", "tshirtfactory", "fixie-01", 4)),
          Right(Product("buyer1", "tshirtfactory", "patagonia-01", 1)),
          Right(Product("buyer1", "jewelry", "pandora-01", 5)),
          Right(Product("buyer1", "jewelry", "guess-01", 4)),
          Right(Product("buyer1", "jewelry", "kors-01", 2)),
          Right(Product("buyer1", "electroshop", "tv50-01", 4)),
          Right(Product("buyer1", "electroshop", "smarttv-01", 2)),
          Right(Product("buyer1", "electroshop", "widetv-03", 4)),
          Left(RatingInvalid)
        )

        val summary = ProductsService.getProductRatingSummary(eitherProducts)

        summary.validLines shouldBe 13
        summary.invalidLines shouldBe 1
        summary.bestRatedProducts shouldBe List("lights-02", "pandora-01", "lights-01")
        summary.worstRatedProducts shouldBe List("patagonia-01", "endura-01", "kors-01")
        summary.mostRatedProducts shouldBe List("lights-02", "smarttv-01", "kors-01")
        summary.lessRatedProducts shouldBe List("lights-01", "endura-01", "guess-01")
      }
    }

    "calling getProductsRatingsSummary on valid csv content" should {
      "return the summary" in {
        val productsRaw = Right(List(
          List("buyer1", "veloshop", "chain-01", "4"),
          List("buyer1", "veloshop", "lights-02", "5"),
          List("buyer1", "veloshop", "lights-01", "5"),
          List("buyer1", "veloshop", "saddle-01", "3")
        ))

        val productCSVParserMock = mock[IProductParser]
        when(productCSVParserMock.readAllProducts("/somePath")).thenReturn(productsRaw)
        val productsService = new ProductsService(productCSVParserMock)

        val eitherSummary = productsService.getProductsRatingsSummary("/somePath")

        eitherSummary.isRight shouldBe true
        eitherSummary.foreach { summary =>
          summary.validLines shouldBe 4
          summary.invalidLines shouldBe 0
          summary.bestRatedProducts shouldBe List("lights-02", "lights-01", "chain-01")
          summary.worstRatedProducts shouldBe List("saddle-01", "chain-01", "lights-01")
          summary.mostRatedProducts shouldBe List("lights-02", "saddle-01", "chain-01")
          summary.lessRatedProducts shouldBe List("lights-01", "chain-01", "saddle-01")
        }
      }
    }

    "calling getProductsRatingsSummary on invalid csv content" should {
      "return the summary" in {
        val productsRaw = Right(List(
          List("buyer1", "veloshop", "chain-01", "4"),
          List("buyer1", "veloshop", "lights-02", "5"),
          List("buyer1", "veloshop", "lights-01", "5"),
          List("buyer1", "veloshop", "saddle-01", "3")
        ))

        val productCSVParserMock = mock[IProductParser]
        when(productCSVParserMock.readAllProducts("/somePath")).thenReturn(Left(new FileNotFoundException("file not found")))
        val productsService = new ProductsService(productCSVParserMock)

        val eitherSummary = productsService.getProductsRatingsSummary("/somePath")

        eitherSummary.isLeft shouldBe true
        eitherSummary match {
          case Left(_: FileNotFoundException) => true
          case _ => fail()
        }
      }
    }
  }
}