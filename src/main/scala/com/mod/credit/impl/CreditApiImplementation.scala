package com.mod.credit.impl

import com.mod.credit.service.{CreditApi, Dao}
import com.mod.credit.{Offer, Request, _}

import scala.concurrent.duration._

/**
  * Created by mehmetoguzdivilioglu on 29/01/2017.
  */

class CreditApiImplementation(dao: Dao) extends CreditApi {
  def createLoanRequest(amount: BigDecimal, duration: Duration) = {
    val requestID = getUID
    dao.insertRequest(Request(requestID, amount, duration.toDays))
    requestID
  }

  def createLoanOffer(requestID: String, amount: BigDecimal, interest: BigDecimal): String = {
    val offerID = getUID
    dao.insertOffer(Offer(offerID, requestID, amount, interest))
    requestID
  }

  def getCurrentOffer(requestID: String): Either[String, Offer] = {
    for {
      offers <- dao.getOffersPerRequest(requestID).right
      request <- dao.getRequest(requestID).right
      partialOffers <- Right(getAccumulatedOffers(request, offers)).right
      bruttoLoanWithInterest <- Right(getLoanWithInterest(partialOffers)).right
      netLoanWithoutInterest <- Right(getTotalLoan(partialOffers)).right
      interestRate <- Right(generateInterestRate(bruttoLoanWithInterest, netLoanWithoutInterest)).right
    } yield Offer(NON_USED_IDENTIFIER, NON_USED_IDENTIFIER, netLoanWithoutInterest, interestRate)
  }
}
