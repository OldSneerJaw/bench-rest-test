package com.oldsneerjaw

import play.api.http._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * An API client for the Bench Rest Test REST API: http://resttest.bench.co/
  */
class BenchApiClient(wsClient: WSClient) {
  private val apiBaseUrl = "http://resttest.bench.co/transactions"

  /**
    * Retrieves a single page of transactions from the RESTful endpoint
    *
    * @param pageNumber The number of the page to retrieve
    *
    * @return A future transaction result summary, or None if the page does not exist
    */
  def fetchResultPage(pageNumber: Long): Future[Option[TransactionResultSummary]] = {
    val url = s"$apiBaseUrl/$pageNumber.json"
    wsClient.url(url).get() map { response =>
      if (response.status != Status.OK) {
        // The page of results does not exist
        None
      } else {
        response.json.validate[TransactionResultSummary] match {
          case jsSuccess: JsSuccess[TransactionResultSummary] => Option(jsSuccess.value)
          case jsError: JsError =>
            println(s"Received an unexpected response from the server for request $url: ${jsError.toString}")
            None
        }
      }
    }
  }
}
