package com.eshop.qa.utils

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.influxdb.InfluxDB
import org.influxdb.dto.Point

import java.util.concurrent.TimeUnit

class WriteMetricToInfluxDB(simulationName: String) {

  def writeError(influxdb: InfluxDB, requestName: String): ChainBuilder = {
    exec(session => {
      val measurementName = s"gatling.$simulationName.$requestName.error"
      val fieldName = "value"
      val statusCode = session("statusCode").as[Int]
      val responseBody = session("responseBody").as[String]
      val error = s"$statusCode $responseBody"
      writeToInfluxDB(requestName, error, measurementName, fieldName, influxdb)
      session
    })
  }

  private def writeToInfluxDB(requestName: String, fieldValue: String, measurementName: String, fieldName: String, influxdb: InfluxDB): Unit = {
    val point = Point.measurement(measurementName)
      .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
      .tag("test_name", simulationName)
      .tag("request_name", requestName)
      .addField(fieldName, fieldValue)
      .build()
    influxdb.write(point)
  }
}