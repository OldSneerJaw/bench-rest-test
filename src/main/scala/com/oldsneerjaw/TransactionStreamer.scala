package com.oldsneerjaw

import scala.concurrent._

/**
  * Responsible for fetching all transactions.
  *
  * @param benchApiClient the API client
  */
class TransactionStreamer(benchApiClient: BenchApiClient)(implicit executionContext: ExecutionContext) {

  /**
    * Retrieves a stream of all transaction pages. The use of a stream ensures that the entire collection is not held in memory at once.
    *
    * @return The stream of transaction pages
    */
  def fetchAllTransactionPages(): Future[Stream[TransactionPage]] = {
    // Create a sequence of page numbers to attempt to retrieve
    // TODO Use a stream that terminates when we've run out of pages to fetch rather than a finite collection
    val pageNumbers = 1 to 99999

    // Map/fold over all possible page numbers to fetch the complete collection of transactions
    val initialState = Future.successful((Stream[TransactionPage](), false))
    val futureFetchedPages = pageNumbers.foldLeft(initialState) { (futureState, pageNumber) =>
      futureState flatMap { currentState =>
        val (fetchedPages, reachedEnd) = currentState

        if (reachedEnd) {
          // We reached the last page of results in a previous iteration, so return the collection as it is
          futureState
        } else {
          benchApiClient.fetchResultPage(pageNumber) map {
            case None =>
              // The page does not exist so signal we've reached the end
              (fetchedPages, true)
            case Some(page) => (fetchedPages :+ page, false)
          }
        }
      }
    }

    // Return only the transaction pages
    futureFetchedPages.map { result => result._1 }
  }
}
