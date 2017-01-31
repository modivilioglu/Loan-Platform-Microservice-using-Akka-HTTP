package com.mod.credit.http

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.stream.ActorMaterializer
import com.mod.credit._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent._
import scala.concurrent.duration._

/**
  * Created by mehmetoguzdivilioglu on 31/01/2017.
  */
class HttpCreditApiServerSpec extends FlatSpec with BeforeAndAfterAll with Matchers {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val requestFormat = jsonFormat3(Request.apply)
  implicit val offerFormat = jsonFormat4(Offer.apply)

  override def beforeAll = {
    HttpCreditApiServer.run
  }

  override def afterAll = {
    HttpCreditApiServer.stop
  }

  "Given request parameters the microservice " should " create request and return success " in {
    val jsonFormat = Request(NON_USED_IDENTIFIER, 1000, 100).toJson

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://localhost:8080/request", entity = HttpEntity(ContentType(MediaTypes.`application/json`), jsonFormat.toString())))

    val response = Await.result(responseFuture, 5 second)
    response.status should be(StatusCode.int2StatusCode(200))

  }
  "Given request and offers the microservice" should " create offer and return success  " in {
    val jsonFormat = Offer(NON_USED_IDENTIFIER, NON_USED_IDENTIFIER, 500, 0.02).toJson

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://localhost:8080/offer", entity = HttpEntity(ContentType(MediaTypes.`application/json`), jsonFormat.toString())))

    val response = Await.result(responseFuture, 5 second)
    response.status should be(StatusCode.int2StatusCode(200))
  }
  "With request and offers created, Given request id, microservice " should "calculate create a loan offer for that request " in {
    implicit val requestFormat = jsonFormat3(Request.apply)
    implicit val offerFormat: RootJsonFormat[Offer] = jsonFormat4(Offer.apply)
    val jsonFormat = Request(NON_USED_IDENTIFIER, 1000, 100).toJson
    implicit val materializer = ActorMaterializer()

    val jsonFuture = for {
      requestIdResponse <- Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://localhost:8080/request", entity = HttpEntity(ContentType(MediaTypes.`application/json`), jsonFormat.toString())))
      requestId <- Unmarshal(requestIdResponse.entity).to[String]
      offerFuture <- Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://localhost:8080/offer", entity = HttpEntity(ContentType(MediaTypes.`application/json`), Offer(NON_USED_IDENTIFIER, requestId, 500, 0.06).toJson.toString)))
      offerFuture <- Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"http://localhost:8080/offer", entity = HttpEntity(ContentType(MediaTypes.`application/json`), Offer(NON_USED_IDENTIFIER, requestId, 500, 0.02).toJson.toString)))
      currentOfferFuture <- Http().singleRequest(HttpRequest(method = HttpMethods.GET, uri = s"http://localhost:8080/request/$requestId"))
      jsonString <- Unmarshal(currentOfferFuture.entity).to[String]
    } yield jsonString


    val jsonOffer = Await.result(jsonFuture, 5 second)
    val offerResponse = jsonOffer.parseJson.convertTo[Offer]
    offerResponse.interest should be(0.04)

  }
}
