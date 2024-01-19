package com.eshop.qa.models

import com.eshop.qa.utils.{ConfigUtil, Randomizer}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.{HttpHeaderNames, HttpHeaderValues}

import scala.collection.immutable.Iterable
import scala.concurrent.duration._
import scala.util.Random

class AbstractPage extends ConfigUtil{
  def printErrorMessage(variable: String, message: String): ChainBuilder = {
    exec(session => {
      val variableValue =
        if (session.attributes.getOrElse(variable, null) != null)
          session(variable).asOption[String].toString
        else
          ""
      logger.error(message + " -> " + variableValue.trim)
      session
    })
  }

  def printSessionAttribute(attribute: String): ChainBuilder =
    exec(session => {
      val value =
        if (session.attributes.contains(attribute))
          if (session(attribute).isInstanceOf[Iterable[_]])
            session(attribute).as[Seq[Any]].mkString
          else
            session(attribute).as[String]
        else
          ""
      logger.info(attribute + " -> " + value)
      session
    })

  val headers_general: Map[CharSequence, String] = Map(
    HttpHeaderNames.ContentType -> HttpHeaderValues.ApplicationJson,
    HttpHeaderNames.Accept -> HttpHeaderValues.ApplicationJson
  )

  protected def setSessionVariablesForRemovingItem(): ChainBuilder = exec(
    session =>
      session
        .set("selectedProductId", Randomizer.getIntFromSeq(session("productsInCart").as[Seq[Int]]))
  )

  protected def setSessionVariablesForUpdatingItem(): ChainBuilder = exec(
    session =>
      session
        .set("quantity", Randomizer.getInt(2, 10))
        .set("selectedProductId", Randomizer.getIntFromSeq(session("productsInCart").as[Seq[Int]]))
  )

  protected def setSessionVariablesForCheckout(): ChainBuilder = exec(
    session =>
      session
        .set("name", Randomizer.getName())
        .set("email", Randomizer.getEmail())
        .set("phone", Randomizer.getInt(9000000, 9999999))
        .set("address", Randomizer.getAddress())
        .set("ccNumber", Randomizer.getInt(1000000, 9999999))
  )
}