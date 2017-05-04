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
    "produce the correct total when there are transactions" in new TestScope {
      val transactionSummaries = Seq(
        TransactionResultSummary(0, 1, Seq(TransactionInfo(exampleDate, "", "15.90", ""), TransactionInfo(exampleDate, "", "-32.03", ""))),
        TransactionResultSummary(0, 2, Seq(TransactionInfo(exampleDate, "", "-4", ""), TransactionInfo(exampleDate, "", "invalid-number", ""))))

      val result = calculator.calculateTotal(transactionSummaries)

      result mustEqual BigDecimal("-20.13")
    }

    "produce a total of zero when there are no transaction summaries" in new TestScope {
      val result = calculator.calculateTotal(Seq.empty)

      result mustEqual BigDecimal(0)
    }

    "produce a total of zero when the transaction summaries have no transactions" in new TestScope {
      val transactionSummaries = Seq(TransactionResultSummary(0, 1, Seq.empty), TransactionResultSummary(0, 2, Seq.empty))

      val result = calculator.calculateTotal(transactionSummaries)

      result mustEqual BigDecimal(0)
    }
  }
}
