package main

import com.google.inject.Guice
import com.typesafe.scalalogging.LazyLogging
import csvparser.IProductParser
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import modules.Module
import net.codingwell.scalaguice.InjectorExtensions._
import services.IProductsService

object ProductsRatingsMain extends Greeting with App with LazyLogging{
  private val injector = Guice.createInjector(new Module())

  private val productCSVParser = injector.instance[IProductParser]
  private val productsService = injector.instance[IProductsService]

  val productsRaw = productCSVParser.readAllProducts()
  val productsRatingsSummary = productsService.getProductsRatingsSummary(productsRaw)

  private val productsRatingsSummaryJson = s"${productsRatingsSummary.asJson.toString()}"
  logger.info(s"Products ratings summary: $productsRatingsSummaryJson")
}

trait Greeting {
  lazy val greeting: String = "hello"
}
