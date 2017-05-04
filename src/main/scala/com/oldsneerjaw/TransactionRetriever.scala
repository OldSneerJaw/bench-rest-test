package com.oldsneerjaw

import scala.concurrent._

/**
  * Responsible for fetching all transactions from the API.
  *
  * @param benchApiClient the API client
  */
class TransactionRetriever(benchApiClient: BenchApiClient)(implicit executionContext: ExecutionContext) {

  /**
    * Retrieves a stream of all transaction pages.
    *
    * @return The transaction pages as a stream
    */
  def fetchAllTransactionPages(): Future[Stream[TransactionPage]] = {
    // Create a sequence of page numbers to attempt to retrieve
    benchApiClient.fetchResultPage(1) flatMap {
      case None => Future.successful(Stream.empty)
      case Some(firstPage) if firstPage.transactions.isEmpty =>
        // It should be safe to assume that, if the first page has no transactions, there won't be any subsequent pages
        Future.successful(Stream(firstPage))
      case Some(firstPage) if firstPage.transactions.nonEmpty =>
        // Determine the total number of pages - assuming that subsequent pages will have the same number of transactions as the first page
        // (except possibly for the last page, which may have fewer transactions)
        val numPages = Math.ceil(firstPage.totalCount.toFloat / firstPage.transactions.size).toInt

        // Fold over all possible page numbers to fetch the complete collection of transactions
        val pageNumbers = (2 to numPages).toStream
        val subsequentPageFutures = pageNumbers.map { pageNumber =>
          benchApiClient.fetchResultPage(pageNumber) map {
            case Some(page) => Option(page)
            case None => None
          }
        }

        val allPageFutures = subsequentPageFutures.#::(Future.successful(Option(firstPage)))

        Future.sequence(allPageFutures) map { pageOptions =>
          // Only include pages that were not missing (i.e. None)
          pageOptions collect {
            case Some(page) => page
          }
        }
    }
  }
}
