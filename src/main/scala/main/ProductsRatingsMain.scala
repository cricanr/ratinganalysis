package main

import com.google.inject.Guice
import com.typesafe.scalalogging.LazyLogging
import models.ProductsRatingsSummary
import modules.Module
import net.codingwell.scalaguice.InjectorExtensions._
import services.IProductsService

object ProductsRatingsMain extends Greeting with App with LazyLogging {
  private val injector = Guice.createInjector(new Module())
  private val productsService = injector.instance[IProductsService]

  def printProductsRatingsSummaryJson(): Unit = {
    val eitherProductsRatingsSummary = productsService.getProductsRatingsSummary

    eitherProductsRatingsSummary match {
      case Right(productsRatingsSummary) =>
        val productsRatingsSummaryJson = s"${ProductsRatingsSummary.getProductsRatingsSummaryAsJson(productsRatingsSummary)}"
        println(s"Products ratings summary: \n$productsRatingsSummaryJson")
      case Left(failure) =>
        println(s"We could not calculate a products ratings summary for the given csv file, an error occurred," +
          s"details: ${failure.getClass.getSimpleName}: ${failure.getMessage}")
    }
  }

  printProductsRatingsSummaryJson()
}

trait Greeting {
  lazy val greeting: String = "hello"
}
