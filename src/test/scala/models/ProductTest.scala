package models

import org.scalatest.{Matchers, WordSpec}

import scala.util.Either.LeftProjection

class ProductTest extends WordSpec with Matchers {
  "The Product" when {
    "given valid parameters" should {
      "be valid" in {
        val product = Product("buyer1", "shop1", "smart-tv-01", 4)

        product shouldBe Product("buyer1", "shop1", "smart-tv-01", 4)
      }
    }
  }

  "given valid 'BuyerId` (contains numbers)" should {
    "return a validation success" in {
      val validatedBuyerId = ProductValidator.validateBuyerId("buyer1Id")
      validatedBuyerId.isRight shouldBe true
    }
  }

  "given valid 'BuyerId` (just characters)" should {
    "return a validation success" in {
      val validatedBuyerId = ProductValidator.validateBuyerId("buyerId")
      validatedBuyerId.isRight shouldBe true
    }
  }

  "given invalid 'BuyerId` (starts with digit)" should {
    "return a validation failure" in {
      val validatedBuyerId = ProductValidator.validateBuyerId("1buyerId")
      validatedBuyerId.isLeft shouldBe true
      validatedBuyerId.left shouldBe LeftProjection(Left(BuyerIdInvalid))
    }
  }

  "given invalid 'BuyerId` (contains special chars)" should {
    "return a validation failure" in {
      val validatedBuyerId = ProductValidator.validateBuyerId("b;uyerId")
      validatedBuyerId.isLeft shouldBe true
      validatedBuyerId.left shouldBe LeftProjection(Left(BuyerIdInvalid))
    }
  }

  "given valid 'ShopId` (contains numbers)" should {
    "return a validation success" in {
      val validatedShopId = ProductValidator.validateShopId("shopId1")
      validatedShopId.isRight shouldBe true
    }
  }

  "given valid 'ShopId` (just characters)" should {
    "return a validation success" in {
      val validatedShopId = ProductValidator.validateShopId("shopId")
      validatedShopId.isRight shouldBe true
    }
  }

  "given invalid 'ShopId` (starts with digit)" should {
    "return a validation failure" in {
      val validatedShopId = ProductValidator.validateShopId("1shopId")
      validatedShopId.isLeft shouldBe true
      validatedShopId.left shouldBe LeftProjection(Left(ShopIdInvalid))
    }
  }

  "given invalid 'ShopId` (contains special chars)" should {
    "return a validation failure" in {
      val validatedShopId = ProductValidator.validateShopId("s_hopId")
      validatedShopId.isLeft shouldBe true
      validatedShopId.left shouldBe LeftProjection(Left(ShopIdInvalid))
    }
  }

  "given valid `Rating` values" should {
    "return a validation success" in {
      for ( i <- 1 to 5) {
        ProductValidator.validateRating(i) match {
          case Right(rating) => rating shouldBe i
        }
      }
    }
  }

  "given invalid `Rating` values" should {
    "return a validation failure" in {
      val invalidValues = Seq(0, 6, 100, 23, 83)
      invalidValues.foreach { i =>
        ProductValidator.validateRating(i) match {
          case Left(invalid) => invalid shouldBe RatingInvalid
        }
      }
    }
  }
}
