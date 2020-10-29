package csvparser

import java.io.FileNotFoundException

import com.typesafe.config.Config
import org.mockito.Mockito.when
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar

class ProductCSVParserTest extends WordSpec with Matchers with MockitoSugar {
  "The Product CSV Parser" when {
    "reading all file lines for a valid file" should {
      "return the product lines parsed" in {
        implicit val configMock: Config = mock[Config]
        when(configMock.getString("csvFilePath")).thenReturn("src/test/resources/validRatings.csv")

        val productCSVParser = new ProductCSVParser()
        val productsRaw = productCSVParser.readAllProducts()

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
        implicit val configMock: Config = mock[Config]
        when(configMock.getString("csvFilePath")).thenReturn("src/test/resources/emptyRatingsWithHeaders.csv")

        val productCSVParser = new ProductCSVParser()
        val productsRaw = productCSVParser.readAllProducts()

        productsRaw shouldBe Right(List.empty)
      }
    }

    "reading all file lines for a valid empty file without headers" should {
      "return an empty parsed list" in {
        implicit val configMock: Config = mock[Config]
        when(configMock.getString("csvFilePath")).thenReturn("src/test/resources/emptyRatingsWithoutHeaders.csv")

        val productCSVParser = new ProductCSVParser()
        val productsRaw = productCSVParser.readAllProducts()

        productsRaw shouldBe Right(List.empty)
      }
    }

    "reading all file lines for an invalid file" should {
      "return a failure" in {
        implicit val configMock: Config = mock[Config]
        when(configMock.getString("csvFilePath")).thenReturn("src/test/resources/noneExisting.csv")
        val productCSVParser = new ProductCSVParser()
        val eitherProductCSVParser = productCSVParser.readAllProducts()
        eitherProductCSVParser.isLeft shouldBe true
        eitherProductCSVParser match {
          case Left(_: FileNotFoundException) => true
          case _ => fail()
        }
      }
    }
  }
}
