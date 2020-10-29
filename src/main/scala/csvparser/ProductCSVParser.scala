package csvparser

import java.io.File

import com.github.tototoshi.csv._
import com.google.inject.Inject
import com.typesafe.config.Config
import models.Product

import scala.util.Try

trait IProductParser {
  def getReader: Either[Throwable, CSVReader]

  def readAllProducts(): Either[Throwable, List[List[String]]]
}

class ProductCSVParser @Inject()(implicit config: Config) extends IProductParser {
  private val reader = getReader

  def getReader: Either[Throwable, CSVReader] = {
    Try {
      val csvFilePath = config.getString("csvFilePath")
      CSVReader.open(new File(csvFilePath))
    }.toEither
  }

  def readAllProducts(): Either[Throwable, List[List[String]]] = {
    reader.map(readerCsv => readerCsv.all().drop(1))
  }
}

object ProductCSVParser {
  def getProductFromCsvLine(productCsvLine: List[String]): Either[Throwable, Product] = {
    Try {
      Product(productCsvLine.head, productCsvLine(1), productCsvLine(2), productCsvLine(3).toInt)
    }.toEither
  }
}
