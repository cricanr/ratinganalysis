package csvparser

import java.io.File

import com.github.tototoshi.csv._
import com.google.inject.Inject
import models.Product

import scala.util.Try

trait IProductParser {
  def readAllProducts(
      csvFilePath: String
  ): Either[Throwable, List[List[String]]]
}

class ProductCSVParser @Inject() extends IProductParser {
  def getReader(csvFilePath: String): Either[Throwable, CSVReader] = {
    Try {
      CSVReader.open(new File(csvFilePath))
    }.toEither
  }

  def readAllProducts(
      csvFilePath: String
  ): Either[Throwable, List[List[String]]] = {
    val reader = getReader(csvFilePath)
    reader.map(readerCsv => readerCsv.all().drop(1))
  }
}

object ProductCSVParser {
  def getProductFromCsvLine(
      productCsvLine: List[String]
  ): Either[Throwable, Product] = {
    Try {
      Product(
        productCsvLine.head,
        productCsvLine(1),
        productCsvLine(2),
        productCsvLine(3).toInt
      )
    }.toEither
  }
}
