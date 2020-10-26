package main

import com.google.inject.Guice
import com.typesafe.scalalogging.LazyLogging
import modules.Module
import net.codingwell.scalaguice.InjectorExtensions._
import services.IProductsService

object ProductsRatingsMain extends Greeting with App with LazyLogging{
  private val injector = Guice.createInjector(new Module())
  private val productsService = injector.instance[IProductsService]

  def printProductsRatingsSummaryJson(): Unit = {
    val productsRaw = productsService.getRawProducts
    val productsRatingsSummary = productsService.getProductsRatingsSummary(productsRaw)

    val productsRatingsSummaryJson = s"${productsService.getProductsRatingsSummaryAsJson(productsRatingsSummary)}"
    println(s"Products ratings summary: $productsRatingsSummaryJson")
  }

  printProductsRatingsSummaryJson()
}

trait Greeting {
  lazy val greeting: String = "hello"
}
