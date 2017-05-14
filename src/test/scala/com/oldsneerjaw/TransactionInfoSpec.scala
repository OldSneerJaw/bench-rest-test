package com.oldsneerjaw

import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.matcher._
import play.api.libs.json._
import play.api.test._

class TransactionInfoSpec extends PlaySpecification {
  "Transaction info" should {

    trait TestScope extends Scope {
      val testObject =
        TransactionInfo(new DateTime(2017, 5, 14, 0, 0, DateTimeZone.UTC), "my-ledger", BigDecimal(77.8), "my-company")

      val testJson = Json.obj("Date" -> "2017-05-14", "Ledger" -> "my-ledger", "Amount" -> "77.80", "Company" -> "my-company")
    }

    "serialize to JSON" in new TestScope {
      val result = Json.toJson(testObject)

      result mustEqual testJson
    }

    "deserialize from JSON" in new TestScope {
      val result = Json.fromJson[TransactionInfo](testJson)

      result mustEqual JsSuccess(testObject)
    }
  }
}
