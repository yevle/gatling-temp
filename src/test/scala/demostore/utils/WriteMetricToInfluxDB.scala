package demostore.utils

import io.gatling.core.Predef._
import com.influxdb.client.{InfluxDBClient, WriteApiBlocking}
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import io.gatling.core.structure.ChainBuilder

import java.time.{Duration, Instant}
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.HashMap
import scala.concurrent.duration.DurationLong

class WriteMetricToInfluxDB(org: String, bucket: String) {

  private var counterByRequest = new HashMap[String, AtomicInteger]()

  def writeResponseTime(influxdb: InfluxDBClient, testName: String, requestName: String): ChainBuilder = {
    exec(session => {
      val measurementName = "response_times"
      val fieldName = "response_time"
      val responseTime = session("responseTime").as[Int]
      writeToInfluxDB(testName, requestName, responseTime, measurementName, fieldName, influxdb)
      session
    })
  }

  def writeThroughput(influxdb: InfluxDBClient, testName: String, requestName: String, secondsPauseBeforeRequest: Long): ChainBuilder = {
    counterByRequest.put(testName, new AtomicInteger(0))
    exec(session => {
      counterByRequest(testName).incrementAndGet()
      val testDuration = System.currentTimeMillis().millisecond.toMillis - Duration.ofMillis(session("startTime").as[Long]).toMillis - secondsPauseBeforeRequest * 1000
      val throughput = ((counterByRequest(testName).toString.toDouble / testDuration) * 1000).toInt
      val measurementName = "throughput"
      writeToInfluxDB(testName, requestName, throughput, measurementName, "throughput", influxdb)
      session
    })
  }

  private def writeToInfluxDB(testName: String, requestName: String, fieldValue: Int, measurementName: String, fieldName: String, influxdb: InfluxDBClient): Unit = {
    val point = Point
      .measurement(measurementName)
      .addTag("test_name", testName)
      .addTag("request_name", requestName)
      .addField(fieldName, fieldValue)
      .time(Instant.now(), WritePrecision.NS)

    val writeApi = influxdb.getWriteApiBlocking
    writeApi.writePoint(bucket, org, point)
  }

}

