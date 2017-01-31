package com.mod.credit.http

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.mod.credit._
import com.mod.credit.impl._
import com.mod.credit.service._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by mehmetoguzdivilioglu on 30/01/2017.
  */
object HttpCreditApiServer {
  val api: CreditApi = new CreditApiImplementation(new InMemoryDAO)
  implicit val requestFormat = jsonFormat3(Request.apply)
  implicit val offerFormat = jsonFormat4(Offer.apply)

  import akka.http.scaladsl.Http
  import akka.http.scaladsl.Http.ServerBinding

  val route = {
    pathPrefix("request") {
      post {
        entity(as[Request]) { request =>
          val requestID = api.createLoanRequest(request.amount, Duration(request.durationInDays, DAYS))

          complete(requestID)
        }
      } ~ (get & path(Segment)) { requestId =>
        val offer = api.getCurrentOffer(requestId)

        complete(offer)
      }
    } ~
      path("offer") {
        post {
          entity(as[Offer]) { offer =>
            val offerID = api.createLoanOffer(offer.requestID, offer.amount, offer.interest)
            complete(offerID)
          }
        }

      }

  }
  var serverFuture: Future[ServerBinding] = _

  def run(implicit system: ActorSystem, executor: ExecutionContextExecutor, materializer: ActorMaterializer) = {
    serverFuture = Http().bindAndHandle(route, "localhost", 8080)
  }

  def stop(implicit system: ActorSystem, executor: ExecutionContextExecutor, materializer: ActorMaterializer) = {
    serverFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def main(args: Array[String]): Unit = {
      implicit val system = ActorSystem()
      implicit val executor = system.dispatcher
      implicit val materializer = ActorMaterializer()

      run
      println("Loan server is running at http://localhost:8080\nPress RETURN to stop ...")
      readLine()
      stop
    }
}
