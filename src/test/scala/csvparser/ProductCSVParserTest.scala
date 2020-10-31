package csvparser

import java.io.FileNotFoundException

import models.Product
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar

class ProductCSVParserTest extends WordSpec with Matchers with MockitoSugar {
  "The Product CSV Parser" when {
    "reading all file lines for a valid file" should {
      "return the product lines parsed" in {
        val csvFilePath = "src/test/resources/validRatings.csv"

        val productCSVParser = new ProductCSVParser
        val productsRaw = productCSVParser.readAllProducts(csvFilePath)

        productsRaw shouldBe Right(List(
          List("buyer1", "veloshop", "chain-01", "4"),
          List("buyer1", "veloshop", "lights-02", "5"),
          List("buyer1", "veloshop", "lights-01", "5"),
          List("buyer1", "veloshop", "saddle-01", "3")
        ))
      }
    }

    "reading all file lines for a valid empty file with headers only" should {
      "return an empty parsed list" in {
        val csvFilePath = "src/test/resources/emptyRatingsWithHeaders.csv"

        val productCSVParser = new ProductCSVParser
        val productsRaw = productCSVParser.readAllProducts(csvFilePath)

        productsRaw shouldBe Right(List.empty)
      }
    }

    "reading all file lines for a valid empty file without headers" should {
      "return an empty parsed list" in {
        val csvFilePath = "src/test/resources/emptyRatingsWithoutHeaders.csv"

        val productCSVParser = new ProductCSVParser
        val productsRaw = productCSVParser.readAllProducts(csvFilePath)

        productsRaw shouldBe Right(List.empty)
      }
    }

    "reading all file lines for an invalid file" should {
      "return a failure" in {
        val csvFilePath = "src/test/resources/noneExisting.csv"
        val productCSVParser = new ProductCSVParser
        val eitherProductCSVParser = productCSVParser.readAllProducts(csvFilePath)
        eitherProductCSVParser.isLeft shouldBe true
        eitherProductCSVParser match {
          case Left(_: FileNotFoundException) => true
          case _ => fail()
        }
      }
    }

    "creating a Product from a valid product raw csv line" should {
      "return the Product" in {
        val productRaw = List("buyer1", "veloshop", "chain-01", "4")
        ProductCSVParser.getProductFromCsvLine(productRaw) shouldBe Right(Product("buyer1", "veloshop", "chain-01", 4))
      }
    }

    "creating a Product from a valid product raw csv line with more columns then needed" should {
      "return the Product" in {
        val productRaw = List("buyer1", "veloshop", "chain-01", "4", "tesla-model-3")
        ProductCSVParser.getProductFromCsvLine(productRaw) shouldBe Right(Product("buyer1", "veloshop", "chain-01", 4))
      }
    }

    "creating a Product from an empty product raw csv line" should {
      "return a failure" in {
        val productRaw = List.empty
        val eitherProduct = ProductCSVParser.getProductFromCsvLine(productRaw)
        eitherProduct.isLeft shouldBe true
        eitherProduct match {
          case Left(_: NoSuchElementException) => true
          case _ => fail()
        }
      }
    }

    "creating a Product from invalid products raw csv line (one property missing)" should {
      "return a failure" in {
        val invalidProductsRaw = List(
          List("veloshop", "chain-01", "4"),
          List("buyer1", "chain-01", "4"),
          List("buyer1", "veloshop", "4"),
          List("buyer1", "veloshop", "chain-01")
        )
        invalidProductsRaw.map { invalidProductRaw =>
          val eitherProduct = ProductCSVParser.getProductFromCsvLine(invalidProductRaw)
          eitherProduct.isLeft shouldBe true
          eitherProduct match {
            case Left(_: IndexOutOfBoundsException) => true
            case _ => fail()
          }
        }
      }
    }
  }
}
