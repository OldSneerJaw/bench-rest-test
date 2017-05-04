package com.oldsneerjaw

import com.oldsneerjaw.IsoDateTimeFormatter._
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * A single transaction (e.g. income or expense).
  *
  * @param date the date of the transaction
  * @param ledger the ledger to which the transaction is assigned
  * @param amount the amount in dollars as a string
  * @param company the company associated with the transaction
  */
case class TransactionInfo(date: DateTime, ledger: String, amount: String, company: String)

object TransactionInfo {
  // Custom JSON serializer because the JSON field names don't match those of the Scala class
  implicit val jsonWrites: Writes[TransactionInfo] = (
    (__ \ "Date").write[DateTime] and
    (__ \ "Ledger").write[String] and
    (__ \ "Amount").write[String] and
    (__ \ "Company").write[String]
  )(unlift(TransactionInfo.unapply))

  // Custom JSON deserializer because the JSON field names don't match those of the Scala class
  implicit val jsonReads: Reads[TransactionInfo] = (
    (__ \ "Date").read[DateTime] and
    (__ \ "Ledger").read[String] and
    (__ \ "Amount").read[String] and
    (__ \ "Company").read[String]
  )(TransactionInfo.apply _)
}
