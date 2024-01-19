package com.eshop.qa.models

import com.eshop.qa.utils.{DbClient, JsonFormatter, Randomizer, RequestManipulator}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object CartPage extends AbstractPage {
  private def addItemToCartHttpRqBuilder(requestName: String): HttpRequestBuilder =
    http(requestName)
      .post("/eshop/control/cart/additem")
      .body(ElFileBody("requests/json/addItemToCart.json")).asJson
      .headers(headers_general)

  def addItemToCart(requestName: String): ChainBuilder = exec(
    addItemToCartHttpRqBuilder(requestName)
      .check(
        status is 200
      )
  )


  private def getCartInfoHttpRqBuilder(requestName: String): HttpRequestBuilder =
    http(requestName)
      .get("/eshop/control/cart/getInfo")
      .headers(headers_general)

  def getCartInfo(requestName: String): ChainBuilder = exec(
    getCartInfoHttpRqBuilder(requestName)
      .check(
        status is 200,
        jsonPath("$.cartInfo.cartItems..id").ofType[Int].findAll.saveAs("productsInCart"),
        jmesPath("cartInfo.cartItems[*].[product.id,quantity]").saveAs("IdQuantity")
      )
  )


  private def removeItemFromCartHttpRqBuilder(requestName: String): HttpRequestBuilder =
    http(requestName)
      .delete("/eshop/control/cart/removeitem")
      .check(status.saveAs("statusCode"))
      .body(ElFileBody("requests/json/removeItemFromCart.json")).asJson
      .headers(headers_general)

  def removeItemFromCart(requestName: String): ChainBuilder = exec(
    setSessionVariablesForRemovingItem()
  ).exec(
    removeItemFromCartHttpRqBuilder(requestName)
      .check(
        status is 200,
        jsonPath("$.removedProductId").is(session => session("selectedProductId").as[String])
      ).check(bodyString.saveAs("responseBody"))
  ).exec(session => {
    if (session("statusCode").as[Int] != 200) {
      val responseBody = session("responseBody").as[String] // Retrieve the response body from the session
      println(s"------Response Body /eshop/control/cart/removeitem: ------\n" + JsonFormatter.formatJson(responseBody)) // Print the response body
    }
    session
  })


  private def updateQuantityHttpRqBuilder(requestName: String): HttpRequestBuilder =
    RequestManipulator.saveStatusCodeAndResponseBody(
      http(requestName)
        .post("/eshop/control/cart/updatequantity")
        .body(ElFileBody("requests/json/updateQuantity.json")).asJson
        .headers(headers_general)
    )

  def updateQuantity(requestName: String): ChainBuilder =
    exec(
      setSessionVariablesForUpdatingItem()
    ).exec(
      updateQuantityHttpRqBuilder(requestName)
        .check(
          status is 200,
          jsonPath(session => "$.cartInfo.cartItems[?(@.product.id=="
            + session("selectedProductId").as[String]
            + ")].quantity").is(session => session("quantity").as[String])
        )
    ).exec(DbClient.writeMetricWriter(requestName))
}