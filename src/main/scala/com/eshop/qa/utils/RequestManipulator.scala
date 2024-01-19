package com.eshop.qa.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object RequestManipulator {
  def saveStatusCodeAndResponseBody(request: HttpRequestBuilder): HttpRequestBuilder = {
    request
      .check(status.saveAs("statusCode"))
      .check(bodyString.saveAs("responseBody"))
  }

//    .exec(session => {
//      // Retrieve status code and response body from session
//      val statusCode = session("statusCode").as[Int]
//      val responseBody = session("responseBody").as[String]
//
//      // Check if the request failed (status code is not 200)
//      if (statusCode != 200) {
//        // Add failed request details to the list in the session variable
//        val failedRequestsList = session("failedRequests").asOption[List[(Int, String)]].getOrElse(List.empty)
//        val updatedList = (statusCode, responseBody) :: failedRequestsList
//        println("list: ")
//        updatedList.foreach(println)
//        session.set("failedRequests", updatedList)
//      } else {
//        // No failure, just return the session as is
//        session
//      }
//    })
}