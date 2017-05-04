package com.oldsneerjaw

import org.joda.time.DateTime
import play.api.test._

class IsoDateTimeFormatterSpec extends PlaySpecification {
  "ISO 8091 date formatter" should {
    "convert a valid date string to a Joda date object" in {
      val result = IsoDateTimeFormatter.formatter.parseDateTime("2017-05-03")

      result mustEqual new DateTime(2017, 5, 3, 0, 0)
    }

    "convert a Joda object to a date string" in {
      val date = new DateTime(2017, 5, 3, 7, 49)

      val result = IsoDateTimeFormatter.formatter.print(date)

      result mustEqual "2017-05-03"
    }

    "fail to convert an invalid date string to a Joda date object" in {
      IsoDateTimeFormatter.formatter.parseDateTime("invalid") must throwAn[IllegalArgumentException]
    }
  }
}
