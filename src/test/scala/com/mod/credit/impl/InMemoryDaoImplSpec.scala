package com.mod.credit.impl

import com.mod.credit.service.{CreditApi, Dao}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import com.mod.credit._
/**
  * Created by mehmetoguzdivilioglu on 31/01/2017.
  */
class InMemoryDaoImplSpec extends FlatSpec with BeforeAndAfter with Matchers {
  val COULD_NOT_FIND_REQUEST_ENTITY = "Could not find request entity"
  val COULD_NOT_FIND_OFFERS_PER_REQUEST = "Could not find offers per request: "

  var dao: Dao = _;
  before {
    dao = new InMemoryDAO
  }
  "Right error message for offer" should "be retrieved when no data" in {
    dao.getRequest("someId") match {
      case Left(message) => message should be (COULD_NOT_FIND_REQUEST_ENTITY)
      case _ => fail
    }
  }
  "Right error message for request " should " be retrieved when no data" in {
    val id = "someOtherId"
    dao.getOffersPerRequest("someOtherId") match {
      case Left(message) => message should be (s"$COULD_NOT_FIND_OFFERS_PER_REQUEST$id")
      case Right(_) => fail
    }
  }
  "Get offer after insering" should " work properly " in {
    dao.insertOffer(Offer("someOfferId1", "someRequestId1", 100, 0.01))
    dao.insertOffer(Offer("someOfferId2", "someRequestId1", 200, 0.02))
    dao.insertOffer(Offer("someOfferId3", "someRequestId1", 300, 0.03))
    val orders = dao.getOffersPerRequest("someRequestId1")
    orders.right.get.length should be (3)
    orders.right.get(2).interest should be (0.03)
  }
  "Get request after insering" should " work properly " in {
    dao.insertRequest(Request("someId", 1000, 100))
    dao.getRequest("someId").right.get.amount should be (1000)
  }
}
