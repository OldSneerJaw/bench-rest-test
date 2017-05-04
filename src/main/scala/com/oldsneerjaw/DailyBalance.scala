package com.oldsneerjaw

import org.joda.time.DateTime

/**
  * Account balance at a specific date.
  *
  * @param date The date
  * @param balance The balance at the date
  */
case class DailyBalance(date: DateTime, balance: BigDecimal)
