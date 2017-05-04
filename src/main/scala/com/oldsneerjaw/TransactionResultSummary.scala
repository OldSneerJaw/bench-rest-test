package com.oldsneerjaw

import play.api.libs.json._

/**
  * A single page of transactions.
  *
  * @param totalCount the number of transactions contained within
  * @param page The page number
  * @param transactions The collection of transactions
  */
case class TransactionResultSummary(totalCount: Long, page: Long, transactions: Seq[TransactionInfo])

object TransactionResultSummary {
  implicit val jsonFormat = Json.format[TransactionResultSummary]
}
