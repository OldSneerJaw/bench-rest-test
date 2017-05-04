package com.oldsneerjaw

import play.api.libs.json._

/**
  * A single page of transactions.
  *
  * @param totalCount the number of transactions contained within
  * @param page The page number
  * @param transactions The collection of transactions
  */
case class TransactionPage(totalCount: Long, page: Long, transactions: Seq[TransactionInfo])

object TransactionPage {
  // Automatically converts a transaction summary page to/from JSON because the class' fields have the exact same names and types as in the
  // source API
  implicit val jsonFormat = Json.format[TransactionPage]
}
