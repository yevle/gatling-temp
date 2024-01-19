package com.eshop.qa

import com.eshop.qa.utils.ConfigUtil
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.eshop.qa.models._
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._


class BaseSimulation extends Simulation with ConfigUtil{

}
