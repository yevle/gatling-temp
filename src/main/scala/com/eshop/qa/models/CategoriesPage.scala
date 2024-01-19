package com.eshop.qa.models

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object CategoriesPage extends AbstractPage {
  private def getCategoriesHttpRqBuilder(requestName: String): HttpRequestBuilder =
    http(requestName)
      .get("/eshop/control/category/all")
      .headers(headers_general)

  def getCategories(requestName: String): ChainBuilder = exec(
    getCategoriesHttpRqBuilder(requestName)
      .check(
        status is 200,
        jsonPath("$.categories").exists,
        jsonPath("$.categories").notNull,
        jsonPath("$.categories[*]").count.is(6),
        jsonPath("$..id").findAll.saveAs("categories")
      )
  )

  private def getCategoryProductsHttpRqBuilder(requestName: String): HttpRequestBuilder =
    http(requestName)
      .get("/eshop/control/category/${categories.random()}/products")
      .headers(headers_general)

  def getCategoryProducts(requestName: String): ChainBuilder = exec(
    getCategoryProductsHttpRqBuilder(requestName)
      .check(
        status is 200,
        jsonPath("$..id").findAll.saveAs("products"),
      )
  )
}