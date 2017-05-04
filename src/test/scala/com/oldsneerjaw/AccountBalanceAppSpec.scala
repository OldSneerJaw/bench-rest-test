package com.oldsneerjaw

import play.api.test._

class AccountBalanceAppSpec extends PlaySpecification {
  "The account balance app" should {
    "run without error" in {
      AccountBalanceApp.main(Array()) must not(throwA[Throwable])
    }
  }
}
