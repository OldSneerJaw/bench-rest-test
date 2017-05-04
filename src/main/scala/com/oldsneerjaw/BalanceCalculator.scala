package com.oldsneerjaw

import org.joda.time.DateTime
import play._

import scala.collection.immutable.TreeMap

/**
  * Calculates the total account balance of a collection of transaction summaries.
  */
class BalanceCalculator {

  private val logger = Logger.of(getClass)

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
      parseAmount(transaction.amount)
    }

    allTransactionAmounts.sum
  }

  /**
    * Calculates the daily balances from the given collection of transaction pages.
    *
    * @param allTransactionPages a collection of all of the transaction pages
    *
    * @return A collection of the daily balances
    */
  def calculateDailyBalances(allTransactionPages: Seq[TransactionPage]): Seq[DailyBalance] = {
    // Map all transactions into a single collection
    val allTransactions = allTransactionPages flatMap { _.transactions }

    // Sort all transactions by date
    val sortedTransactions = allTransactions.sortBy(_.date.getMillis)

    val initialTotals = (TreeMap.empty[Long, BigDecimal], BigDecimal(0))
    val (computedDailyBalanceMap, _) = sortedTransactions.foldLeft(initialTotals) { (runningTotals, transaction) =>
      val (dailyBalanceMap, totalBalance) = runningTotals

      val updatedBalance = totalBalance + parseAmount(transaction.amount)
      val filledInDailyBalanceMap = fillInMissingDays(dailyBalanceMap, transaction.date)
      val updatedDailyBalanceMap = filledInDailyBalanceMap.+((transaction.date.getMillis, updatedBalance))

      (updatedDailyBalanceMap, updatedBalance)
    }

    // Convert the map to a sequence
    computedDailyBalanceMap.toSeq map { dailyBalanceEntry =>
      DailyBalance(new DateTime(dailyBalanceEntry._1), dailyBalanceEntry._2)
    }
  }

  private def fillInMissingDays(dailyBalanceMap: TreeMap[Long, BigDecimal], currentDate: DateTime): TreeMap[Long, BigDecimal] = {
    dailyBalanceMap.lastOption match {
      case None =>
        // The map is empty, so add the first day, which should be one day before the current date
        dailyBalanceMap.+((currentDate.minusDays(1).getMillis, BigDecimal(0)))
      case Some(lastEntry) =>
        if (currentDate.minusDays(1).isAfter(lastEntry._1)) {
          val updatedDailyBalanceMap = dailyBalanceMap.+((new DateTime(lastEntry._1).plusDays(1).getMillis, lastEntry._2))
          // Now ensure that we fill in the intervening days recursively as well
          fillInMissingDays(updatedDailyBalanceMap, currentDate)
        } else {
          dailyBalanceMap
        }
    }
  }

  private def parseAmount(amountString: String) = {
    try {
      BigDecimal(amountString)
    } catch {
      case _: NumberFormatException =>
        // Fall back to zero if the number is in an invalid format
        logger.error(s"Invalid transaction amount encountered: $amountString")
        BigDecimal(0)
    }
  }
}
