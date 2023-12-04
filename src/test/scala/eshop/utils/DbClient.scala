package eshop.utils

import org.influxdb.InfluxDBFactory
import org.influxdb.InfluxDB
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object DbClient {
  val url = "http://localhost:8653"
  val username = "admin"
  val password = "admin"
  val database = "graphite"

  val influxDB: InfluxDB = InfluxDBFactory.connect(url, username, password)
  influxDB.setDatabase(database)
  val simulationName: String =
    PropertyConfigurator.getProperty("SIMULATION", "eshop.ParameterizedScenario")
      .split('.')
      .lastOption
      .getOrElse("")
      .toLowerCase()

  val metricWriter = new WriteMetricToInfluxDB(simulationName)

  def writeMetricWriter(requestName: String): ChainBuilder = {
    doIf(session => {
      session("statusCode").as[Int] != 200
    }) {
      metricWriter.writeError(influxDB, requestName)
    }
  }
}