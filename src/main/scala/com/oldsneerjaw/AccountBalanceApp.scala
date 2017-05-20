package com.oldsneerjaw

import java.text.ParseException
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.stream._
import org.apache.http.client.HttpResponseException
import play.api.libs.ws.ahc._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * A simple application that downloads and prints bank account balances to the console.
  */
object AccountBalanceApp extends App {
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val wsClient = AhcWSClient()

  val apiClient = new BenchApiClient(wsClient)
  val transactionRetriever = new TransactionRetriever(apiClient)
  val balanceCalculator = new BalanceCalculator

  val outputFuture = transactionRetriever.fetchAllTransactionPages.map { transactionPages =>
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
  }.recover {
    case ex: ParseException => println(s"Could not parse API response: ${ex.getMessage}")
    case ex: HttpResponseException => println(s"Encountered an unexpected response status (${ex.getStatusCode}): ${ex.getMessage}")
  }

  // Execute the future
  Await.result(outputFuture, Duration(30, TimeUnit.SECONDS))

  // Clean up
  wsClient.close()
  Await.result(actorSystem.terminate(), Duration(10, TimeUnit.SECONDS))
}
