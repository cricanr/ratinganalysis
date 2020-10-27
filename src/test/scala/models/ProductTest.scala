package models

import org.scalatest.{Matchers, WordSpec}

import scala.util.Either.LeftProjection

class ProductTest extends WordSpec with Matchers {
  "The Product" when {
    "given valid parameters" should {
      "be valid" in {
        val validatedProduct = ProductValidator.validateProduct(buyerId = "buyer1",shopId = "shop1", productId = "smart-tv-01", rating = 4)
        validatedProduct.isRight shouldBe true
        validatedProduct.map(prod => prod shouldBe Product(buyerId = "buyer1", shopId = "shop1", productId = "smart-tv-01", rating = 4))
      }
    }

    "given some invalid parameter buyerId" should {
      "be invalid" in {
        val validatedProduct = ProductValidator.validateProduct("2buyer","shop1", "smart-tv-01", 4)
        validatedProduct.isLeft shouldBe true
        validatedProduct.map(prod => prod shouldBe BuyerIdInvalid)
      }
    }

    "given some invalid parameter shopId" should {
      "be invalid" in {
        val validatedProduct = ProductValidator.validateProduct("buyer1","1shop12", "smart-tv-01", 4)
        validatedProduct.isLeft shouldBe true
        validatedProduct.map(prod => prod shouldBe ShopIdInvalid)
      }
    }

    "given some invalid parameter productId" should {
      "be invalid" in {
        val validatedProduct = ProductValidator.validateProduct("buyer1","shop12", "smart[-tv-01", 4)
        validatedProduct.isLeft shouldBe true
        validatedProduct.map(prod => prod shouldBe ProductIdInvalid)
      }
    }

    "given some invalid parameter rating" should {
      "be invalid" in {
        val validatedProduct = ProductValidator.validateProduct("buyer1","shop12", "smart-tv-01", 6)
        validatedProduct.isLeft shouldBe true
        validatedProduct.map(prod => prod shouldBe RatingInvalid)
      }
    }

    "given all invalid parameters" should {
      "be invalid" in {
        val validatedProduct = ProductValidator.validateProduct("1buyer1","1shop12", "1smart-tv-01", 6)
        validatedProduct.isLeft shouldBe true
        validatedProduct.map(prod => prod shouldBe BuyerIdInvalid)
        validatedProduct.map(prod => prod shouldBe ShopIdInvalid)
        validatedProduct.map(prod => prod shouldBe ProductIdInvalid)
        validatedProduct.map(prod => prod shouldBe RatingInvalid)
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
      for ( i <- 1 to 5) { ProductValidator.validateRating(i).map { rating => rating shouldBe i } }
    }
  }

  "given invalid `Rating` values" should {
    "return a validation failure" in {
      val invalidValues = Seq(0, 6, 100, 23, 83)
      invalidValues.foreach { i =>
        ProductValidator.validateRating(i) match {
          case Left(invalid) => invalid shouldBe RatingInvalid
          case _ => fail()
        }
      }
    }
  }

  "given valid `ProductId` values" should {
    "return a validation success" in {
      val validProductIds = Seq("smart-tv-01", "patagonia-32", "northface-99")
      validProductIds.foreach { productId =>
        ProductValidator.validateProductId(productId).map { id => id shouldBe productId } }
      }
    }

  "given invalid `ProductId` values" should {
    "return a validation failure" in {
      val invalidProductIds = Seq("1smart-tv-01", "p[atagonia-32", "northface-100", "burton-00", "libtech-0", "union-bindings-202")
      invalidProductIds.foreach { productId =>
        ProductValidator.validateProductId(productId) match {
          case Left(invalid) => invalid shouldBe ProductIdInvalid
          case _ => fail()
        }
      }
    }
  }
}