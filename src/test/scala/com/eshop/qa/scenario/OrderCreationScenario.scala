package com.eshop.qa.scenario

import com.eshop.qa.BaseSimulation
import com.eshop.qa.models.{CartPage, CategoriesPage, CheckoutPage, HomePage}
import com.eshop.qa.utils.Configurator
import com.eshop.qa.utils.PropertyConfigurator.getProperty
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import java.time.LocalDateTime

case class OrderCreationScenario() extends BaseSimulation {
  var startTime: LocalDateTime = LocalDateTime.now()

  private val httpConf: HttpProtocolBuilder = http
    .baseUrl(Configurator.url)
  def userCount: Int = getProperty("USERS", Configurator.usersCount.toString).toInt

  def rampUpDuration: Int = getProperty("RAMP_DURATION", Configurator.rampUpDurationSeconds.toString).toInt

  def testDuration: Int = getProperty("TEST_DURATION", Configurator.testDurationSeconds.toString).toInt



  val scn = scenario(getClass.getSimpleName)
    .exec(HomePage.getIndexPage("01_getIndexPage"))
    .exec(CategoriesPage.getCategories("02_getCategories"))
    .exec(CategoriesPage.getCategoryProducts("03_getCategoryProducts"))
    .exec(CartPage.addItemToCart("04_addItemToCart"))
    .exec(CategoriesPage.getCategoryProducts("05_getCategoryProducts"))
    .exec(CartPage.addItemToCart("06_addItemToCart"))
    .exec(CartPage.getCartInfo("07_getCartInfo"))
    .exec(CartPage.updateQuantity("08_updateQuantity"))
    .exec(CartPage.getCartInfo("09_getCartInfo"))
    .exec(CartPage.removeItemFromCart("10_removeItemFromCart"))
    .exec(CheckoutPage.submitPurchase("11_submitPurchase"))

  val populationBuilder = scn
    .inject(rampUsers(userCount) during rampUpDuration)
    .protocols(httpConf)

  setUp(populationBuilder)
}
