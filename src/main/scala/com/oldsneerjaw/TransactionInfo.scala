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
case class TransactionInfo(date: DateTime, ledger: String, amount: BigDecimal, company: String)

object TransactionInfo {
  // Custom JSON serializer because the JSON field names don't match those of the Scala class
  implicit val jsonWrites = new Writes[TransactionInfo] {
    override def writes(o: TransactionInfo): JsValue =
      Json.obj("Date" -> o.date, "Ledger" -> o.ledger, "Amount" -> o.amount.setScale(2).toString, "Company" -> o.company)
  }

  // Custom JSON deserializer using a basic combinator (https://playframework.com/documentation/2.5.x/ScalaJsonCombinators) because, even
  // though the JSON field names don't match the Scala class field names, the amount value can be converted from a string to a BigDecimal
  // automatically
  implicit val jsonReads: Reads[TransactionInfo] = (
    (__ \ "Date").read[DateTime] and
    (__ \ "Ledger").read[String] and
    (__ \ "Amount").read[BigDecimal] and
    (__ \ "Company").read[String]
  )(TransactionInfo.apply _)
}
