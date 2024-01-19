package com.eshop.qa.simulation

import com.eshop.qa.scenario.OrderCreationChain
import com.eshop.qa.utils.PropertyConfigurator.getProperty
import com.eshop.qa.utils.{Configurator, ScenarioInjector, SlackNotificator}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.PopulationBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import java.time.LocalDateTime

class ParameterizedScenario extends Simulation{
  var startTime: LocalDateTime = LocalDateTime.now()

  private val httpConf: HttpProtocolBuilder = http
    .baseUrl(Configurator.url)
  def userCount: Int = getProperty("USERS", Configurator.usersCount.toString).toInt
  def rampUpDuration: Int = getProperty("RAMP_DURATION", Configurator.rampUpDurationSeconds.toString).toInt
  def testDuration: Int = getProperty("TEST_DURATION", Configurator.testDurationSeconds.toString).toInt
  before {
    startTime = LocalDateTime.now()
    println(s"Test started at: $startTime")
  }

  private val asserts = Seq(
    global.responseTime.percentile3.lte(5000)
  )
  private val orderCreation_OpenScenario: PopulationBuilder = {
    scenario("OrderCreation_Scenario_OpenModel")
      .during(testDuration)(OrderCreationChain.execute)
      .inject(ScenarioInjector.injectOpenModel(userCount, rampUpDuration))
      .protocols(httpConf)
      .andThen(
        SlackNotificator.sendNotificationAboutFinish(startTime, LocalDateTime.now().plusSeconds(testDuration))
          .inject(
            atOnceUsers(1)
          )
          .protocols(SlackNotificator.getSlackHTTPProtocol)
      )
  }
  setUp(
//    SlackNotificator.sendNotificationAboutStart()
//      .inject(atOnceUsers(1))
//      .protocols(SlackNotificator.getSlackHTTPProtocol),
    orderCreation_OpenScenario
//        .andThen(
//          SlackNotificator.sendNotificationAboutFinish(startTime, LocalDateTime.now())
//          .inject(atOnceUsers(1))
//          .protocols(SlackNotificator.getSlackHTTPProtocol)
//        )
  ).assertions(asserts)

  after {

  }
}