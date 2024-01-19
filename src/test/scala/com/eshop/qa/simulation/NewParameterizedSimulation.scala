package com.eshop.qa.simulation

import com.eshop.qa.scenario.OrderCreationScenario
import com.eshop.qa.utils.Configurator
import com.eshop.qa.utils.PropertyConfigurator.getProperty
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.PopulationBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import java.time.LocalDateTime

class NewParameterizedSimulation extends Simulation{

  private val asserts = Seq(
    global.responseTime.percentile3.lte(5000)
  )

  setUp(
    OrderCreationScenario().populationBuilder
  ).assertions(asserts)

}