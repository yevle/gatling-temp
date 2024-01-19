package com.eshop.qa.models

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object HomePage extends AbstractPage {
  private def getIndexPageHttpRqBuilder(requestName: String): HttpRequestBuilder =
    http(requestName)
      .get("/eshop")
      .headers(headers_general)

  def getIndexPage(requestName: String): ChainBuilder = exec(
    getIndexPageHttpRqBuilder(requestName)
      .check(
        status is 200
      )
  )
}