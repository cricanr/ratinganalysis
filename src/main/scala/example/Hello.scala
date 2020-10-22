package example

import java.io.File


object Hello extends Greeting with App {
  println(greeting)

  import com.github.tototoshi.csv._
  val reader = CSVReader.open(new File("src/main/resources/ratings.csv"))
  val ratings = reader.all()

  println(ratings)
}

trait Greeting {
  lazy val greeting: String = "hello"
}
