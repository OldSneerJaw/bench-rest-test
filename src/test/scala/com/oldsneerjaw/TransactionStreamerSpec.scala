package com.oldsneerjaw

import org.joda.time.DateTime
import org.specs2.mock._
import org.specs2.specification._
import play.api.test._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class TransactionStreamerSpec extends PlaySpecification with Mockito {
  trait TestScope extends Scope {
    val mockBenchClient = mock[BenchApiClient].smart

    val transactionStreamer = new TransactionStreamer(mockBenchClient)
  }

  "Account streamer" should {
    "retrieve all transaction pages" in new TestScope {
      val page1 = TransactionPage(1, 1, Seq(TransactionInfo(new DateTime(2017, 5, 3, 1, 1), "ledger1", "amount1", "company1")))
      val page2 = TransactionPage(1, 1, Seq(TransactionInfo(new DateTime(2017, 5, 3, 2, 2), "ledger2", "amount2", "company2")))

      mockBenchClient.fetchResultPage(1) returns Future.successful(Option(page1))
      mockBenchClient.fetchResultPage(2) returns Future.successful(Option(page2))
      mockBenchClient.fetchResultPage(3) returns Future.successful(None)

      val result = await(transactionStreamer.fetchAllTransactionPages())

      result mustEqual Stream(page1, page2)

      there was one(mockBenchClient).fetchResultPage(1)
      there was one(mockBenchClient).fetchResultPage(2)
      there was one(mockBenchClient).fetchResultPage(3)
      there was no(mockBenchClient).fetchResultPage(4)
    }
  }
}
