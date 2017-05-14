package com.oldsneerjaw

import org.joda.time.DateTime
import org.specs2.specification._
import play.api.test._

class BalanceCalculatorSpec extends PlaySpecification {
  trait TestScope extends Scope {
    val exampleDate = new DateTime(2017, 5, 3, 20, 49)
    val calculator = new BalanceCalculator()
  }

  "Balance calculator" should {
    "when calculating the total balance" >> {
      "produce the correct total when there are transactions" in new TestScope {
        val transactionPages = Seq(
          TransactionPage(0, 1, Seq(TransactionInfo(exampleDate, "", BigDecimal(15.9), ""), TransactionInfo(exampleDate, "", BigDecimal(-32.03), ""))),
          TransactionPage(0, 2, Seq(TransactionInfo(exampleDate, "", BigDecimal(-4), ""), TransactionInfo(exampleDate, "", BigDecimal(0), ""))))

        val result = calculator.calculateTotal(transactionPages)

        result mustEqual BigDecimal("-20.13")
      }

      "produce a total of zero when there are no transaction summaries" in new TestScope {
        val result = calculator.calculateTotal(Seq.empty)

        result mustEqual BigDecimal(0)
      }

      "produce a total of zero when the transaction summaries have no transactions" in new TestScope {
        val transactionPages = Seq(TransactionPage(0, 1, Seq.empty), TransactionPage(0, 2, Seq.empty))

        val result = calculator.calculateTotal(transactionPages)

        result mustEqual BigDecimal(0)
      }
    }

    "when calculating the daily balances" >> {
      "report the correct balances" in new TestScope {
        val date1 = new DateTime(2017, 5, 2, 0, 0)
        val page1 = TransactionPage(4, 1, Seq(TransactionInfo(date1, "ledger1", BigDecimal(15.9), "company1")))

        val date2 = new DateTime(2017, 5, 3, 0, 0)
        val page2 = TransactionPage(4, 2, Seq(TransactionInfo(date2, "ledger2", BigDecimal(16.77), "company2")))

        val date3 = new DateTime(2017, 4, 29, 0, 0)
        val page3 = TransactionPage(4, 3, Seq(TransactionInfo(date3, "ledger3", BigDecimal(-42.85), "company3")))

        val page4 = TransactionPage(4, 4, Seq(TransactionInfo(date1, "ledger4", BigDecimal(11.13), "company4")))

        val transactionPages = Seq(page1, page2, page3, page4)

        val result = calculator.calculateDailyBalances(transactionPages)

        result mustEqual Seq(
          DailyBalance(new DateTime(2017, 4, 28, 0, 0), BigDecimal(0)),
          DailyBalance(date3, BigDecimal("-42.85")),
          DailyBalance(new DateTime(2017, 4, 30, 0, 0), BigDecimal("-42.85")),
          DailyBalance(new DateTime(2017, 5, 1, 0, 0), BigDecimal("-42.85")),
          DailyBalance(date1, BigDecimal("-15.82")),
          DailyBalance(date2, BigDecimal("0.95")))
      }

      "return nothing if there are no transactions" in new TestScope {
        val transactionPages = Seq(TransactionPage(0, 1, Seq.empty))

        val result = calculator.calculateDailyBalances(transactionPages)

        result mustEqual Seq.empty
      }
    }
  }
}
