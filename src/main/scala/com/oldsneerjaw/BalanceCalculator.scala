package com.oldsneerjaw

import org.joda.time.{DateTime, Duration}

import scala.collection.mutable

/**
  * Calculates the total account balance of a collection of transaction summaries.
  */
class BalanceCalculator {

  private type DailyBalanceEntry = (Long, BigDecimal)

  /**
    * Determines the total balance taking into account all transactions.
    *
    * @param allTransactionPages a collection of all of the transaction pages
    *
    * @return The total balance
    */
  def calculateTotal(allTransactionPages: Seq[TransactionPage]): BigDecimal = {
    // Map all transactions into a single collection
    val allTransactions = allTransactionPages.flatMap(_.transactions)

    allTransactions.map(_.amount).sum
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
    val allTransactions = allTransactionPages.flatMap(_.transactions)

    // Sort all transactions by date
    val sortedTransactions = allTransactions.sortBy(_.date.getMillis)

    // Uses a linked hash map to store the daily balances for two reasons: (1) to make retrieving and updating the existing balance for a
    // given day a constant time operation and (2) so that chronological order of transactions is preserved from the sort performed earlier
    val dailyBalanceMap = new mutable.LinkedHashMap[Long, BigDecimal]

    sortedTransactions.foldLeft[Option[DailyBalanceEntry]](None) { (previousEntryOption, transaction) =>
      val runningBalance = previousEntryOption.map(_._2).getOrElse(BigDecimal(0))

      val updatedBalance = runningBalance + transaction.amount
      val currentEntry = new DailyBalanceEntry(transaction.date.getMillis, updatedBalance)

      dailyBalanceMap ++= getMissingDays(transaction.date, previousEntryOption) += currentEntry

      Option(currentEntry)
    }

    // Convert the map to a sequence
    dailyBalanceMap.toSeq map { dailyBalanceEntry =>
      DailyBalance(new DateTime(dailyBalanceEntry._1), dailyBalanceEntry._2)
    }
  }

  private def getMissingDays(currentDate: DateTime, previousEntryOption: Option[DailyBalanceEntry]): Seq[DailyBalanceEntry] = {
    previousEntryOption match {
      case None =>
        // The map is empty, so add the first day, which should be one day before the current date
        Seq(currentDate.minusDays(1).getMillis -> BigDecimal(0))
      case Some(lastEntry) =>
        val lastDate = new DateTime(lastEntry._1)
        if (currentDate.minusDays(1).isAfter(lastDate)) {
          // One or more intervening days had no transactions - fill them in with the same balance as the previous transaction date
          val duration = new Duration(lastDate, currentDate)
          val durationRange = 1 to duration.getStandardDays.toInt
          durationRange map { days =>
            new DailyBalanceEntry(lastDate.plusDays(days).getMillis, lastEntry._2)
          }
        } else {
          // There are no missing days to fill in
          Seq.empty
        }
    }
  }
}

