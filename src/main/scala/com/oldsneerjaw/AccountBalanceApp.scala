package com.oldsneerjaw

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.stream._
import play.api.libs.json._
import play.api.libs.ws.ahc._

import scala.concurrent._
import scala.concurrent.duration._

/**
  * A simple application that downloads and prints account balances to the console.
  */
object AccountBalanceApp extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val wsClient = AhcWSClient()

  val apiClient = new BenchApiClient(wsClient)

  val futureResults = apiClient.fetchResultPage(1)

  Await result(futureResults, Duration(10, TimeUnit.SECONDS)) match {
    case None => println("No results returned")
    case Some(results) => println(s"Results: ${Json.toJson(results)}")
  }

  wsClient.close()
}
