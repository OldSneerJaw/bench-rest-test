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
  val transactionStreamer = new TransactionStreamer(apiClient)
  val balanceCalculator = new BalanceCalculator()

  val futureResults = transactionStreamer.fetchAllTransactionPages()

  val totaledFutureResults = futureResults map { results =>
    balanceCalculator.calculateTotal(results)
  }

  val total = Await result(totaledFutureResults, Duration(10, TimeUnit.SECONDS))

  println(s"Total balance: $total")

  wsClient.close()
  actorSystem.terminate()
}
