package com.oldsneerjaw

/**
  * Calculates the total balance of a collection of transaction summaries.
  */
class BalanceCalculator {
  def calculateTotal(allTransactionSummaries: Seq[TransactionResultSummary]): BigDecimal = {
    // Map all transactions into a single collection
    val allTransactions = allTransactionSummaries flatMap { _.transactions }

    // Convert the transaction amounts to BigDecimal values
    val allTransactionAmounts = allTransactions map { transaction =>
      try {
        BigDecimal(transaction.amount)
      } catch {
        case _: NumberFormatException =>
          // Fall back to zero if the number is in an invalid format
          println(s"Invalid transaction amount encountered: ${transaction.amount}")
          BigDecimal(0)
      }
    }

    allTransactionAmounts.sum
  }
}
