package eshop

import eshop.chains.OrderCreationChain
import eshop.utils.PropertyConfigurator.getProperty
import eshop.utils.{Configurator, ScenarioInjector, SlackNotificator}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class ParameterizedScenario extends Simulation{
  private val httpConf: HttpProtocolBuilder = http
    .baseUrl(Configurator.url)
  before(
    println(s"userCount: $userCount\nrampUpDuration: $rampUpDuration\ntestDuration: $testDuration\nsimName: $simulationName")
  )
  def userCount: Int = getProperty("USERS", Configurator.usersCount.toString).toInt
  def rampUpDuration: Int = getProperty("RAMP_DURATION", Configurator.rampUpDurationSeconds.toString).toInt
  def testDuration: Int = getProperty("TEST_DURATION", Configurator.testDurationSeconds.toString).toInt
  def simulationName: String = getProperty("SIMULATION", "eshop.ParameterizedScenario")


  private val asserts = Seq(
    global.responseTime.percentile3.lte(5000)
  )
  private val orderCreation_OpenScenario: ScenarioBuilder = {
    scenario("OrderCreation_Scenario_OpenModel")
      .during(testDuration)(OrderCreationChain.execute)
  }
  setUp(
    orderCreation_OpenScenario
      .inject(ScenarioInjector.injectOpenModel(userCount, rampUpDuration))
      .protocols(httpConf)
//    SlackNotificator.getSetUpper("/triggers/T4E815KGA/6273744595475/f6ca30500eb4b1d2761fcccc20f7d99b")
  ).assertions(asserts)

}