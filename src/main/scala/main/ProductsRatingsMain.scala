package main

import com.google.inject.Guice
import models.ProductsRatingsSummary
import modules.Module
import net.codingwell.scalaguice.InjectorExtensions._
import services.IProductsService

object ProductsRatingsMain extends App {
  private def getProductsService: IProductsService = {
    val injector = Guice.createInjector(new Module())
    injector.instance[IProductsService]
  }

  def printProductsRatingsSummaryJson(): Unit = {
    println(calculate("src/main/resources/ratings.csv"))
  }

  def calculate(csvFilePath: String): String = {
    val productsService = getProductsService
    val eitherProductsRatingsSummary =
      productsService.getProductsRatingsSummary(csvFilePath)

    eitherProductsRatingsSummary match {
      case Right(productsRatingsSummary) =>
        ProductsRatingsSummary.getProductsRatingsSummaryAsJson(
          productsRatingsSummary
        )
      case Left(failure) =>
        s"We could not calculate a products ratings summary for the given csv file, an error occurred," +
          s"details: ${failure.getClass.getSimpleName}: ${failure.getMessage}"
    }
  }

  printProductsRatingsSummaryJson()
}
