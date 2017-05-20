package com.oldsneerjaw

import java.io.IOException

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
      val page1 = TransactionPage(3, 1, Seq(TransactionInfo(new DateTime(2017, 5, 3, 1, 1), "ledger1", BigDecimal(-75.1), "company1")))
      val page2 = TransactionPage(3, 2, Seq(TransactionInfo(new DateTime(2017, 5, 3, 2, 2), "ledger2", BigDecimal(33), "company2")))
      val page3 = TransactionPage(3, 3, Seq(TransactionInfo(new DateTime(2017, 5, 3, 3, 3), "ledger3", BigDecimal(0.79), "company3")))

      mockBenchClient.fetchResultPage(1) returns Future(Option(page1))
      mockBenchClient.fetchResultPage(2) returns Future(Option(page2))
      mockBenchClient.fetchResultPage(3) returns Future(Option(page3))
      mockBenchClient.fetchResultPage(4) returns Future(None)

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result mustEqual Seq(page1, page2, page3)

      there was one(mockBenchClient).fetchResultPage(1)
      there was one(mockBenchClient).fetchResultPage(2)
      there was one(mockBenchClient).fetchResultPage(3)
      there was no(mockBenchClient).fetchResultPage(4)
    }

    "retrieve all transaction pages when the first page has more transactions" in new TestScope {
      val page1 = TransactionPage(3, 1, Seq(
        TransactionInfo(new DateTime(2017, 5, 3, 1, 1), "ledger1", BigDecimal(8.9), "company1"),
        TransactionInfo(new DateTime(2017, 5, 3, 2, 2), "ledger2", BigDecimal(-1334.96), "company2")))
      val page2 = TransactionPage(3, 2, Seq(TransactionInfo(new DateTime(2017, 5, 3, 3, 3), "ledger3", BigDecimal(0.5), "company3")))

      mockBenchClient.fetchResultPage(1) returns Future(Option(page1))
      mockBenchClient.fetchResultPage(2) returns Future(Option(page2))
      mockBenchClient.fetchResultPage(3) returns Future(None)

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result mustEqual Seq(page1, page2)

      there was one(mockBenchClient).fetchResultPage(1)
      there was one(mockBenchClient).fetchResultPage(2)
      there was no(mockBenchClient).fetchResultPage(3)
    }

    "retrieve all transaction pages when the last page is missing" in new TestScope {
      val page1 = TransactionPage(3, 1, Seq(TransactionInfo(new DateTime(2017, 5, 3, 1, 1), "ledger1", BigDecimal(6), "company1")))
      val page2 = TransactionPage(3, 2, Seq(TransactionInfo(new DateTime(2017, 5, 3, 2, 2), "ledger2", BigDecimal(-94.77), "company2")))

      mockBenchClient.fetchResultPage(1) returns Future(Option(page1))
      mockBenchClient.fetchResultPage(2) returns Future(Option(page2))
      mockBenchClient.fetchResultPage(3) returns Future(None)

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result mustEqual Seq(page1, page2)

      there was one(mockBenchClient).fetchResultPage(1)
      there was one(mockBenchClient).fetchResultPage(2)
      there was one(mockBenchClient).fetchResultPage(3)
      there was no(mockBenchClient).fetchResultPage(4)
    }

    "return nothing when there are no pages" in new TestScope {
      mockBenchClient.fetchResultPage(anyInt) returns Future(None)

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result mustEqual Seq.empty

      there was one(mockBenchClient).fetchResultPage(1)
      there was no(mockBenchClient).fetchResultPage(2)
    }

    "return nothing when the first page has no transactions" in new TestScope {
      val page1 = TransactionPage(0, 1, Seq.empty)
      mockBenchClient.fetchResultPage(anyInt) returns Future(Option(page1))

      val result = await(transactionRetriever.fetchAllTransactionPages())

      result mustEqual Seq(page1)

      there was one(mockBenchClient).fetchResultPage(1)
      there was no(mockBenchClient).fetchResultPage(2)
    }

    "throw an exception if a server response is invalid" in new TestScope {
      mockBenchClient.fetchResultPage(anyInt) returns Future.failed(new IOException())

      await(transactionRetriever.fetchAllTransactionPages()) must throwAn[IOException]
    }
  }
}
