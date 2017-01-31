package com.mod.credit.service

import com.mod.credit.{Offer, Request}

/**
  * Created by mehmetoguzdivilioglu on 29/01/2017.
  */
trait Dao {
  def insertOffer(offer: Offer): Unit
  def insertRequest(request: Request) : Unit
  def getRequest(identifier: String): Either[String, Request]
  def getOffersPerRequest(id: String): Either[String, List[Offer]]
}
