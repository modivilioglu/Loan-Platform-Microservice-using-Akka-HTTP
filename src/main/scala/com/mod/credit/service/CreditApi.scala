package com.mod.credit.service

import com.mod.credit.Offer

import scala.concurrent.duration.Duration
/**
  * Created by mehmetoguzdivilioglu on 29/01/2017.
  */
trait CreditApi {
  def createLoanRequest(amount: BigDecimal, duration: Duration): String
  def createLoanOffer(requestId: String, amount: BigDecimal, interest: BigDecimal): String
  def getCurrentOffer(requestId: String): Either[String, Offer]
}
