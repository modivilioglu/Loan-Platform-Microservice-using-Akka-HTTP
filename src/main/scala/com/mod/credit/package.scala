package com.mod

import java.util.Date

/**
  * Created by mehmetoguzdivilioglu on 29/01/2017.
  */
package object credit {

  case class Request(identifier: String, amount: BigDecimal, durationInDays: Long)

  case class Offer(identifier: String, requestID: String, amount: BigDecimal, interest: BigDecimal)

  val NON_USED_IDENTIFIER = "NON_USED_IDENTIFIER"

  def getUID: String = new Date().getTime.toString

  def getLoanWithInterest(offers: List[Offer]) = offers.foldLeft(BigDecimal(0))((acc, offer) => acc + offer.amount * (1 + offer.interest))

  def getTotalLoan(offers: List[Offer]) = offers.map(o => o.amount).sum

  def generateInterestRate(totalLoanWithInterest: BigDecimal, totalLoan: BigDecimal): BigDecimal = {
    (totalLoanWithInterest / totalLoan) - 1
  }

  def getAccumulatedOffers(request: Request, remainingOffers: List[Offer]) = {
    accumulateOffersPerRequest(request, remainingOffers, List(), 0)
  }

  private def accumulateOffersPerRequest(request: Request,
                                         remaining: List[Offer],
                                         acc: List[Offer],
                                         soFar: BigDecimal): List[Offer] = {
    remaining match {
      case Nil => acc
      case (x :: xs) if (soFar + x.amount > request.amount) => Offer(NON_USED_IDENTIFIER, NON_USED_IDENTIFIER, request.amount - soFar, remaining.head.interest) :: acc
      case (x :: xs) if (soFar + x.amount > request.amount) => acc
      case (x :: xs) => accumulateOffersPerRequest(request, remaining.tail, remaining.head :: acc, soFar + remaining.head.amount)
    }
  }
}
