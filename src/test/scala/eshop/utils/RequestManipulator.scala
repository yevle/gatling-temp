package eshop.utils

import io.gatling.http.Predef._
import io.gatling.core.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object RequestManipulator {
  def saveStatusCodeAndResponseBody(request: HttpRequestBuilder): HttpRequestBuilder = {
    request
      .check(status.saveAs("statusCode"))
      .check(bodyString.saveAs("responseBody"))
  }
}