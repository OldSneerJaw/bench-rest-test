package com.oldsneerjaw

/**
  * Calculates the total account balance of a collection of transaction summaries.
  */
class BalanceCalculator {

  /**
    * Determines the total balance taking into account all transactions.
    *
    * @param allTransactionPages a collection of all of the transaction pages
    *
    * @return The total balance
    */
  def calculateTotal(allTransactionPages: Seq[TransactionPage]): BigDecimal = {
    // Map all transactions into a single collection
    val allTransactions = allTransactionPages flatMap { _.transactions }

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
