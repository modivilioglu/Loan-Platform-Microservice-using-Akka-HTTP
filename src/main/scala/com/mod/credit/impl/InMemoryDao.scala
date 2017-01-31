package com.mod.credit.impl

import com.mod.credit.service.Dao
import com.mod.credit.{Offer, Request}

/**
  * Created by mehmetoguzdivilioglu on 29/01/2017.
  */
class InMemoryDAO extends Dao {
  var offers: Map[String, List[Offer]] = Map[String, List[Offer]]()
  // PartitionByRequestId
  var requests: Map[String, Request] = Map[String, Request]()

  val COULD_NOT_FIND_REQUEST_ENTITY = "Could not find request entity"
  val COULD_NOT_FIND_OFFERS_PER_REQUEST = "Could not find offers per request: "

  def insertOffer(offer: Offer): Unit = {
    val offersPerRequest = offers.get(offer.requestID) //getOffers In Partition
    val result = offersPerRequest match {
      case None => offers + (offer.requestID -> List(offer))
      case Some(partition) => offers + (offer.requestID -> (offer :: partition))
    }
    offers = result
  }

  def insertRequest(request: Request): Unit = requests = requests + (request.identifier -> request)

  def getRequest(identifier: String): Either[String, Request] = requests.get(identifier) match {
    case None => Left(COULD_NOT_FIND_REQUEST_ENTITY)
    case Some(x) => Right(x)
  }

  def getOffersPerRequest(id: String): Either[String, List[Offer]] = offers.get(id) match {
    case None => Left(s"$COULD_NOT_FIND_OFFERS_PER_REQUEST$id")
    case Some(x) => Right(x.sortWith((offer1, offer2) => offer1.interest < offer2.interest))
  }
}