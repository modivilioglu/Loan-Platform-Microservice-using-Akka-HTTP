## Synopsis

This is a Scala Project, based on microservices to build a loan platform. Individuals will be
able to create loan request with an API call, create loan offers for that loan request as well, and get current loan offers
by calculation of the available offers for that specific loan request.

## Motivation:

We have been asked to build a crowd-sourcing loan platform which will allow borrowers to take out loans that are made
up of multiple loans from different lenders. Users who want to borrow money (borrowers) will enter a loan request, which consists of the amount they want to
borrow and the duration they want to borrow the money for. Users who want to lend money (lenders) will enter the
amount of money they are willing to lend towards this loan and the interest rate (APR) they require.

## Client Code Example:

From the business layer client's perspective the commands can be called as below:
```
    val api: CreditApi = new CreditApiImplementation(new InMemoryDAO)
    val requestId = api.createLoanRequest(1000, Duration(100, DAYS))
    println(requestId);
    api.createLoanOffer(requestId, 100, 0.12);
    api.createLoanOffer(requestId, 200, 0.50);
    val result = api.getCurrentOffer(requestId)
    result match {
      case Left(x) => println(x)
      case Right(curretOffer) => println(s"${curretOffer.amount} ${curretOffer.interest}")
    }
```
From the http layer client's perspective the commands can be called as below:
(Proper usage should be with curl)
```
val jsonFormat = Request(NON_USED_IDENTIFIER, 1000, 100).toJson

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://localhost:8080/request", entity = HttpEntity(ContentType(MediaTypes.`application/json`), jsonFormat.toString())))

    val response = Await.result(responseFuture, 5 second)
    response.status should be(StatusCode.int2StatusCode(200))
```
## Installation:
```
sh> git clone https://github.com/modivilioglu/loan-platform-microservice-using-Akka-HTTP.git
sh> cd loan-platform-microservice-using-Akka-HTTP
sh> sbt run

## Usage
To start up the microservice simply type
sh> sbt run

The microservice is up and running on localhost:8080 and via curl commands, you will be
able to
- Create loan requests
curl -X POST -H "Accept: text/plain" -H "Content-Type: application/json" -H "Cache-Control: no-cache" -d '{
	"identifier": "NONE",
	"amount": 1000,
	"durationInDays": 100
}' "http://localhost:8080/request"

- Create loan offers for request
curl -X POST -H "Accept: text/plain" -H "Content-Type: application/json" -H "Cache-Control: no-cache" -d '{
	"identifier": "2",
	"requestID": "1485825123198",
	"amount": 400,
	"interest": 0.02
}' "http://localhost:8080/offer"

- Get current loan per offers made for that specific request
curl -X GET -H "Content-Type: application/json" -H "Cache-Control: no-cache"  "http://localhost:8080/request/1485819097636"

## Tests

There are test cases for business layer CreditApi, dao layer InMemoryDao, as well as Microservice layer HttpCreditApiServer. You can run them using the following command
```sh
sh> sbt test
```

## Used frameworks, libraries and assumptions

For this service, two assumpsions have been made
- This is going to be a REST based microservice
- An inmemory database with no latency will be used

A leightweight framework is selected for microservices.
Akka-HTTP is a perfect fit for this, and Play Framework is not used
since we do not need its browser view functionality.

Spray library is used to convert to and from Json format, which is
the de facto library on Akka, and also widely used in Scala ecosystem.

## Technical Notes
The code consists of Dao, Business and HTTP layers.

HTTP layers are simple REST calls using the business layer apis.

Business Layer simply implements the basic business logic, using help from
utility functions in the package object.

An in memory database is generated:
List for requests and HashMap for offers. The Hashmap is partitioned by request Id, as
offers are made per request.

Traits provide decoupling when injecting classes (like injecting dao in
CreditApi)

For comprehensions are used to simply provide a workflow logic instead of
flatmaps, and Either is used instead of Try Monads, to get a better understanding
of generated errors.

Some things have not been implemented, to keep things simpler,
as Xor from cats library could be used instead of Either.

Some side effects could not be avoided as per Dao layer methods,
which returned Unit.

## Contributors

Mehmet Oguz Divilioglu, Email: mo.divilioglu@gmail.com

