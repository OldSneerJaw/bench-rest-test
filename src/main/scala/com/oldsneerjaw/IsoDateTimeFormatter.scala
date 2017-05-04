package com.oldsneerjaw

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.libs.json._

/**
  * Used to format Joda dates as ISO 8091 date strings without time and time zone components (e.g. "2017-05-03").
  */
object IsoDateTimeFormatter {
  val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

  // Converts between Joda dates and JSON
  implicit val jsonFormat: Format[DateTime] = new Format[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] = JsSuccess(formatter.parseDateTime(json.as[String]))
    override def writes(dt: DateTime): JsValue = JsString(formatter.print(dt))
  }
}
