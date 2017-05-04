package com.oldsneerjaw

import mockws.MockWS
import org.joda.time.DateTime
import org.specs2.specification._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.test._

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
      val expectedTransactions = Seq(TransactionInfo(new DateTime(2017, 4, 3, 0, 0), "my-account", "my-amount", "my-company"))
      val expectedTransactionsSummary = TransactionResultSummary(1, pageNumber, expectedTransactions)
      val mockWS = wsClient(pageNumber, Ok(Json.toJson(expectedTransactionsSummary)))

      val result = await(apiClient(mockWS).fetchResultPage(pageNumber))

      result must beSome(expectedTransactionsSummary)
    }

    "return nothing if the page does not exist" in new TestScope {
      val pageNumber = 5
      val mockWS = wsClient(pageNumber, NotFound)

      val result = await(apiClient(mockWS).fetchResultPage(pageNumber))

      result must beNone
    }

    "return nothing if the response could not be parsed" in new TestScope {
      val pageNumber = 7
      val mockWS = wsClient(pageNumber, InternalServerError("Foo!"))

      val result = await(apiClient(mockWS).fetchResultPage(pageNumber))

      result must beNone
    }
  }
}
