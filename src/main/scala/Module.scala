package modules

import com.google.inject.AbstractModule
import csvparser.{IProductParser, ProductCSVParser}
import net.codingwell.scalaguice.ScalaModule
import services.{IProductsService, ProductsService}

class Module extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[IProductsService].to[ProductsService].asEagerSingleton()
    bind[IProductParser].to[ProductCSVParser].asEagerSingleton()
  }
}
