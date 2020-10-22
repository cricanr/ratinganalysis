package models

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxTuple4Semigroupal
import models.ProductValidatorNec.ValidationResult

final case class Product(buyerId: String, shopId: String, productId: String, rating: Int)

object Product {
//  def apply(buyerId: String, shopId: String, productId: String, rating: Int): ValidationResult[Product] = {
    import ProductValidator._
//    (validateBuyerId(buyerId), validateProductId(productId), validateShopId(shopId), validateRating(rating))
//      .mapN((a,b,c,d) => Product(a,b,c,d))
//  }
}

sealed trait DomainValidation {
  def errorMessage: String
}

sealed trait ProductValidator {
  def validateBuyerId(buyerId: String): Either[DomainValidation, String] =
    Either.cond(
      buyerId.matches("^[a-zA-Z][a-zA-Z0-9]*$"),
      buyerId,
      BuyerIdInvalid
    )

    def validateShopId(shopId: String): Either[DomainValidation, String] =
      Either.cond(
        shopId.matches("^[a-zA-Z][a-zA-Z0-9]*$"),
        shopId,
        ShopIdInvalid
      )

  def validateProductId(productId: String): Either[DomainValidation, String] = {
    val maybeNumberAtEnd = productId.substring(productId.lastIndexWhere(p => p == '-')).toIntOption
    Either.cond(
      maybeNumberAtEnd.exists(numberAtEnd => productId.matches("^[a-zA-Z][a-zA-Z]*-[0-9]+$") && numberAtEnd >= 1 && numberAtEnd <= 99),
      productId,
      ProductIdInvalid
    )
  }

  def validateRating(rating: Int): Either[DomainValidation, Int] =
    Either.cond(
      rating >= 1 && rating <= 5,
      rating,
      RatingInvalid
    )
}

sealed trait ProductValidatorNec {
  type ValidationResult[A] = ValidatedNec[DomainValidation, A]


}

object ProductValidatorNec extends ProductValidatorNec

object ProductValidator extends ProductValidator

case object BuyerIdInvalid extends DomainValidation {
  def errorMessage: String = "BuyerId is a sequence of alphanumeric characters that starts with a letter"
}

case object ShopIdInvalid extends DomainValidation {
  def errorMessage: String = "ShopId is a sequence of alphanumeric characters that starts with a letter"
}

case object ProductIdInvalid extends DomainValidation {
  def errorMessage: String = "ProductId is a sequence of alphanumeric characters that starts with a letter"
}

case object RatingInvalid extends DomainValidation {
  def errorMessage: String = "Rating is numeric value between 1 and 5 as a whole number"
}


