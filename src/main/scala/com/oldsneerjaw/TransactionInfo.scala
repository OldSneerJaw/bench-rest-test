package com.oldsneerjaw

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * A single transaction (e.g. income or expense).
  *
  * @param date the date of the transaction
  * @param ledger the ledger/account name
  * @param amount the amount in dollars as a string
  * @param company the company associated with the transaction
  */
case class TransactionInfo(date: String, ledger: String, amount: String, company: String)

object TransactionInfo {
  // Serializes Scala object into JSON
  implicit val jsonWrites: Writes[TransactionInfo] = (
    (__ \ "Date").write[String] and
    (__ \ "Ledger").write[String] and
    (__ \ "Amount").write[String] and
    (__ \ "Company").write[String]
  )(unlift(TransactionInfo.unapply))

  // Deserializes JSON into Scala object
  implicit val jsonReads: Reads[TransactionInfo] = (
    (__ \ "Date").read[String] and
    (__ \ "Ledger").read[String] and
    (__ \ "Amount").read[String] and
    (__ \ "Company").read[String]
  )(TransactionInfo.apply _)
}
