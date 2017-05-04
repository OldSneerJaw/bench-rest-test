package com.oldsneerjaw

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.stream._
import play.api.libs.ws.ahc._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * A simple application that downloads and prints account balances to the console.
  */
object AccountBalanceApp extends App {
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val wsClient = AhcWSClient()

  val apiClient = new BenchApiClient(wsClient)
  val transactionRetriever = new TransactionRetriever(apiClient)
  val balanceCalculator = new BalanceCalculator()

  val futureTransanctionPages = transactionRetriever.fetchAllTransactionPages()

  // Execute the future to retrieve the stream of pages
  val transactionPages = Await result(futureTransanctionPages, Duration(30, TimeUnit.SECONDS))

  val dailyBalances = balanceCalculator.calculateDailyBalances(transactionPages)

  // Previously this relied on `balanceCalculator.calculateTotal`, but the total is already available to us via the last daily balance, so
  // there's no need to duplicate effort
  val totalBalance = dailyBalances.lastOption.map(_.balance).getOrElse(BigDecimal(0))

  // Print out the balances, formatted with two decimal places
  dailyBalances foreach { dailyBalance =>
    val dateString = IsoDateTimeFormatter.formatter.print(dailyBalance.date)
    val formattedBalance = dailyBalance.balance.setScale(2)
    println(s"$dateString: $formattedBalance")
  }

  println(s"Total balance: ${totalBalance.setScale(2)}")

  wsClient.close()
  actorSystem.terminate()
}
