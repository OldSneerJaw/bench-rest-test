package com.oldsneerjaw

import org.joda.time.DateTime
import org.specs2.mock._
import org.specs2.specification._
import play.api.test._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class TransactionRetrieverSpec extends PlaySpecification with Mockito {
  trait TestScope extends Scope {
    val mockBenchClient = mock[BenchApiClient].smart

    val transactionRetriever = new TransactionRetriever(mockBenchClient)
  }

  "Transaction retriever" should {
    "retrieve all transaction pages when there is the same number of transactions on each page" in new TestScope {
      val page1 = TransactionPage(3, 1, Seq(TransactionInfo(new DateTime(2017, 5, 3, 1, 1), "ledger1", "amount1", "company1")))
      val page2 = TransactionPage(3, 2, Seq(TransactionInfo(new DateTime(2017, 5, 3, 2, 2), "ledger2", "amount2", "company2")))
      val page3 = TransactionPage(3, 3, Seq(TransactionInfo(new DateTime(2017, 5, 3, 3, 3), "ledger3", "amount3", "company3")))

      mockBenchClient.fetchResultPage(1) returns Future(Option(page1))
      mockBenchClient.fetchResultPage(2) returns Future(Option(page2))
      mockBenchClient.fetchResultPage(3) returns Future(Option(page3))
      mockBenchClient.fetchResultPage(4) returns Future(None)

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result.toList mustEqual List(page1, page2, page3)

      there was one(mockBenchClient).fetchResultPage(1)
      there was one(mockBenchClient).fetchResultPage(2)
      there was one(mockBenchClient).fetchResultPage(3)
      there was no(mockBenchClient).fetchResultPage(4)
    }

    "retrieve all transaction pages when the first page has more transactions" in new TestScope {
      val page1 = TransactionPage(3, 1, Seq(
        TransactionInfo(new DateTime(2017, 5, 3, 1, 1), "ledger1", "amount1", "company1"),
        TransactionInfo(new DateTime(2017, 5, 3, 2, 2), "ledger2", "amount2", "company2")))
      val page2 = TransactionPage(3, 2, Seq(TransactionInfo(new DateTime(2017, 5, 3, 3, 3), "ledger3", "amount3", "company3")))

      mockBenchClient.fetchResultPage(1) returns Future(Option(page1))
      mockBenchClient.fetchResultPage(2) returns Future(Option(page2))
      mockBenchClient.fetchResultPage(3) returns Future(None)

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result.toList mustEqual List(page1, page2)

      there was one(mockBenchClient).fetchResultPage(1)
      there was one(mockBenchClient).fetchResultPage(2)
      there was no(mockBenchClient).fetchResultPage(3)
    }

    "return nothing when there are no pages" in new TestScope {
      mockBenchClient.fetchResultPage(anyInt) returns Future(None)

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result.toList mustEqual List.empty

      there was one(mockBenchClient).fetchResultPage(1)
      there was no(mockBenchClient).fetchResultPage(2)
    }

    "return nothing when the first page has no transactions" in new TestScope {
      val page1 = TransactionPage(0, 1, Seq.empty)
      mockBenchClient.fetchResultPage(anyInt) returns Future(Option(page1))

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result.toList mustEqual List(page1)

      there was one(mockBenchClient).fetchResultPage(1)
      there was no(mockBenchClient).fetchResultPage(2)
    }
  }
}
