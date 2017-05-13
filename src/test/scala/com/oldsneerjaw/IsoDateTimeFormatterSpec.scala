package com.oldsneerjaw

import org.joda.time.{DateTime, DateTimeZone}
import play.api.test._

class IsoDateTimeFormatterSpec extends PlaySpecification {
  "ISO 8091 date formatter" should {
    "convert a valid date string to a Joda date object" in {
      val result = IsoDateTimeFormatter.formatter.parseDateTime("2017-05-03")

      result mustEqual new DateTime(2017, 5, 3, 0, 0, DateTimeZone.UTC)
    }

    "convert a Joda object to a date string" in {
      // Note that, when converted to UTC, the date string will be 2017-05-03, rather than 2017-05-02 in the -0800 time zone
      val date = new DateTime(2017, 5, 2, 23, 49, DateTimeZone.forOffsetHours(-8))

      val result = IsoDateTimeFormatter.formatter.print(date)

      result mustEqual "2017-05-03"
    }

    "fail to convert an invalid date string to a Joda date object" in {
      IsoDateTimeFormatter.formatter.parseDateTime("invalid") must throwAn[IllegalArgumentException]
    }
  }
}
