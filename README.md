# Introduction

This repo contains a solution to the Bench Rest Test. It was implemented by Joel Andrews using Scala. The project also makes use of Play Framework 2.5's libraries to help with HTTP requests and specifications. As such, the Scala version is limited to 2.11.

# Usage

The project requires Java 6+ to run.

* Run the app to fetch and tally the balances from the API: `./sbt run`
* Run the unit tests: `./sbt test`

# Problem

For reference, here is the original problem description:

> ## Getting Started
>
> Welcome to the Bench Rest Test. The purpose of this exercise is to demonstrate your ability to reason about rudimentary APIs and data transformation. You can use any language you feel comfortable with.
>
> We would like you to write an app that we can run from the command line, which:
>
> 1. Connects to a REST API documented below and fetches all pages of financial transactions.
> 2. Calculates total balance and prints it to the console, where balance is the sum of all amounts in all transactions. For example, if I have 3 transactions each for $4, then the total balance would be $12.
> 3. Calculates running daily balances and prints them to the console. For example, if I have 3 transactions for the 5th 6th 7th, each for $5, then the running daily balance on the 4th would be $0, on the 5th would be $5, on the 6th would be $10, on the 7th it would be $15.
>
> Include unit tests for this application.
>
> To submit your work, create a git repository on Github and send us the link in an email
>
> ## API Documentation
>
> This API provides access to all the transactions in an imaginary bank account.
>
> This is a REST API, providing JSON-formatted data over HTTP.
>
> There is a limit to how many transactions that can be returned in a single request, so the transactions are split into "pages". You will have to download all the pages to get all the data.
>
> `GET http://resttest.bench.co/transactions/{page}.json`
>
> #### Responses
>
> **200 OK**
>
> ```
> {
>   "totalCount": 32, // Integer, total number of transactions across all pages
>   "page": 1, // Integer, current page
>   "transactions": [
>     {
>       "Date": "2013-12-22", // String, date of transaction
>       "Ledger": "Phone & Internet Expense", // String, ledger name
>       "Amount": "-110.71", // String, amount
>       "Company": "SHAW CABLESYSTEMS CALGARY AB" // String, company name
>     },
>     ...
>   ]
> }
> ```
> 
> **404 NOT FOUND**
> 
> _No response body_
> 
> ## Considerations
> 
> 1. Approach this problem as you would in the real-world. Consider errors that may occur when fetching and transforming data from the API, such as non-200 http responses.
> 2. Consider scalability when picking data abstractions and algorithms; what would happen if the transaction list was considerably larger?
> 3. Coding style matters. Ensure your code is consistent and easy to follow. Leave comments where appropriate and use meaningful methods and variables.
> 4. Avoid overly complex code. The complexity of the solution should make sense for the problems you're solving.
> 5. Document limitations and trade-offs of your code if appropriate.
> 6. Include a README explaining how to install and/or run your software.
