package com.oldsneerjaw

import java.io.IOException

import mockws._
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.specification._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global

class BenchApiClientSpec extends PlaySpecification {

  trait TestScope extends Scope {
    val apiBaseUrl = "http://resttest.bench.co/transactions"

    def wsClient(pageNumber: Long, expectedResult: Result): MockWS = {
      val requestUrl = s"$apiBaseUrl/$pageNumber.json"

      MockWS {
        case (GET, `requestUrl`) => mvc.Action { expectedResult }
      }
    }

    def apiClient(mockWS: MockWS) = new BenchApiClient(mockWS)
  }

  "Bench API client" should {
    "return the collection of transactions if they exist" in new TestScope {
      val pageNumber = 2
      val expectedTransactions = Seq(TransactionInfo(new DateTime(2017, 4, 3, 0, 0, DateTimeZone.UTC), "my-account", BigDecimal(3), "my-company"))
      val expectedTransactionPage = TransactionPage(1, pageNumber, expectedTransactions)
      val mockWS = wsClient(pageNumber, Ok(Json.toJson(expectedTransactionPage)))

      val result = await(apiClient(mockWS).fetchResultPage(pageNumber))

      result must beSome(expectedTransactionPage)
    }

    "return nothing if the page does not exist" in new TestScope {
      val pageNumber = 5
      val mockWS = wsClient(pageNumber, NotFound)

      val result = await(apiClient(mockWS).fetchResultPage(pageNumber))

      result must beNone
    }

    "throw an exception if the response body could not be parsed" in new TestScope {
      val pageNumber = 7
      val mockWS = wsClient(pageNumber, Ok("Foo!"))

      await(apiClient(mockWS).fetchResultPage(pageNumber)) must throwAn[IOException]
    }

    "throw an exception if the response status is invalid" in new TestScope {
      val pageNumber = 1
      val mockWS = wsClient(pageNumber, InternalServerError("Foo!"))

      await(apiClient(mockWS).fetchResultPage(pageNumber)) must throwAn[IOException]
    }
  }
}
