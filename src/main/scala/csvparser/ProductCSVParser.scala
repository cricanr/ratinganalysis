package csvparser

import java.io.File
import com.github.tototoshi.csv._
import com.typesafe.config.ConfigFactory

trait IProductParser {
  def readAllProducts(): List[List[String]]
}

class ProductCSVParser extends IProductParser {
  private val config = ConfigFactory.load()
  private val csvFilePath = config.getString("csvFilePath")
  private val reader = CSVReader.open(new File(csvFilePath))

  def readAllProducts(): List[List[String]] = {
    reader.all().drop(1)
  }
}
