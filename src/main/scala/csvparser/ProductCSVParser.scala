package csvparser

import java.io.File

import com.github.tototoshi.csv._
import com.google.inject.Inject
import com.typesafe.config.Config

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
