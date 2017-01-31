package com.mod.credit.impl

import com.mod.credit.service._

import scala.concurrent.duration._

/**
  * Created by mehmetoguzdivilioglu on 31/01/2017.
  */

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class CreditApiImplementationSpec extends FlatSpec with BeforeAndAfter with Matchers {
  var api: CreditApi = _;
  var dao: Dao = _;
  before {
    dao = new InMemoryDAO
    api = new CreditApiImplementation(dao)
  }
  "Insert" should " work correctly" in {
    val requestId = api.createLoanRequest(1000, Duration(10, DAYS))
    requestId should not be (null)
    val requestResult = dao.getRequest(requestId)
    requestResult match {
      case Right(request) => request.amount should be(1000)
      case Left(_) => fail
    }
  }

  "Get Current loan " should "give the correct value" in {
    val requestId = api.createLoanRequest(1000, Duration(10, DAYS))
    val loadId1 = api.createLoanOffer(requestId, 100, 0.05)
    val loadId2 = api.createLoanOffer(requestId, 500, 0.086)
    val offerResult = api.getCurrentOffer(requestId)

    offerResult match {
      case Right(offer) => offer.amount should be(600); offer.interest should be(0.08)
      case Left(_) => fail
    }
  }

  "Get Current loan with 4 offers " should "give the correct value" in {
    val requestId = api.createLoanRequest(1000, Duration(10, DAYS))
    val loadId1 = api.createLoanOffer(requestId, 100, 0.05)
    val loadId2 = api.createLoanOffer(requestId, 600, 0.06)
    val loadId3 = api.createLoanOffer(requestId, 600, 0.07)
    val loadId4 = api.createLoanOffer(requestId, 500, 0.082)
    val offerResult = api.getCurrentOffer(requestId)
    offerResult match {
      case Right(offer) => offer.amount should be(1000); offer.interest should be(0.062);
      case Left(_) => fail
    }

  }

}
