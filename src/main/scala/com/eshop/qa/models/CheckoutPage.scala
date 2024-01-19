package com.eshop.qa.models

import com.eshop.qa.utils.Randomizer
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object CheckoutPage extends AbstractPage {
  def submitPurchase(requestName: String): ChainBuilder = exec(
    setSessionVariablesForCheckout()
  ).exec(
    submitPurchaseHttpRqBuilder(requestName)
      .check(
        status is 200
      )
  )

  private def submitPurchaseHttpRqBuilder(requestName: String): HttpRequestBuilder =
    http(requestName)
      .post("/eshop/control/checkout/purchase/submit")
      .body(ElFileBody("requests/json/submitPurchase.json")).asJson
      .headers(headers_general)
}