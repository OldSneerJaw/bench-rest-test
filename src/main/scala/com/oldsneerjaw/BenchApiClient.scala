package com.oldsneerjaw

import java.text.ParseException

import org.apache.http.client.HttpResponseException
import play.api.http._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent._

/**
  * An API client for the Bench Rest Test REST API: http://resttest.bench.co/
  *
  * @param wsClient The web service client that is used to perform HTTP requests
  */
class BenchApiClient(wsClient: WSClient)(implicit executionContext: ExecutionContext) {

  private val apiBaseUrl = "http://resttest.bench.co/transactions"

  /**
    * Retrieves a single page of transactions from the RESTful endpoint
    *
    * @param pageNumber The number of the page to retrieve
    *
    * @return A future transaction page, or None if the page does not exist. The Future will throw in these cases:
    *         - ParseException: if the response's format is invalid
    *         - HttpResponseException: if an unexpected HTTP response status is received
    */
  def fetchResultPage(pageNumber: Long): Future[Option[TransactionPage]] = {
    val url = s"$apiBaseUrl/$pageNumber.json"
    wsClient.url(url).get() map { response =>
      if (response.status == Status.OK) {
        response.json.validate[TransactionPage] match {
          case jsSuccess: JsSuccess[TransactionPage] => Option(jsSuccess.value)
          case jsError: JsError => throw new ParseException(s"Invalid response body for request $url: ${jsError.toString}", 0)
        }
      } else if (response.status == Status.NOT_FOUND) {
        None
      } else {
        throw new HttpResponseException(response.status, s"Unsupported response status for request $url: ${response.body}")
      }
    }
  }
}
