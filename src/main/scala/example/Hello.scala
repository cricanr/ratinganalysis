package example

import java.io.File
import models.Product

object Hello extends Greeting with App {
  println(greeting)

  import com.github.tototoshi.csv._
  val reader = CSVReader.open(new File("src/main/resources/ratings.csv"))
  val productsRaw = reader.all().drop(1)
  val products = productsRaw.map { product =>
    Product(product.head, product(1), product(2), product(3).toInt)
  }

  println(products)
}

trait Greeting {
  lazy val greeting: String = "hello"
}
