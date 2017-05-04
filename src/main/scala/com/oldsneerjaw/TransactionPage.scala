package com.oldsneerjaw

import play.api.libs.json._

/**
  * A single page of transactions.
  *
  * @param totalCount the total number of transactions from all pages
  * @param page The page number
  * @param transactions The collection of transactions
  */
case class TransactionPage(totalCount: Int, page: Int, transactions: Seq[TransactionInfo])

object TransactionPage {
  // Automatically converts a transaction summary page to/from JSON because the class' fields have the exact same names and types as in the
  // source API (unlike in TransactionInfo where the JSON field names differ from the Scala class field names)
  implicit val jsonFormat = Json.format[TransactionPage]
}
