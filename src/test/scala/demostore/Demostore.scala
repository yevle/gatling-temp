package demostore

import com.influxdb.client.{InfluxDBClient, InfluxDBClientFactory}
import demostore.pageObjects._
import demostore.utils.WriteMetricToInfluxDB
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt
import scala.util.Random

class Demostore extends Simulation {

  // Change credentials from InfluxDB
  val url = "http://localhost:8086"
  val token = "7y03OntLARLN7Atald_mWjb_SLlnBZxDIZRaPbq3lZC6BmOSXWoG6kavGWQrJQX5trQWBH4tT82y714uYoUcvg=="
  val org = "Solvd"
  val bucket = "Gatling"
  val client: InfluxDBClient = InfluxDBClientFactory.create(url, token.toCharArray)

  val metricWriter = new WriteMetricToInfluxDB(org, bucket)


  val DOMAIN = "demostore.gatling.io"
  private val httpProtocol = http
    .baseUrl(s"http://${DOMAIN}")

  def userCount = getProperty("USERS", "5").toInt

  def rampDuration = getProperty("RAMP_DURATION", "5").toInt

  def testDuration = getProperty("TEST_DURATION", "30").toInt

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  val rnd = new Random()

  def rndString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  before {
    println(s"Running simulation with:\n ${userCount} users \n ${rampDuration} ramp duration \n ${testDuration} test duration")
  }

  after {
    println("Test complete!")
  }

  val initSession = exec(flushCookieJar)
    .exec(session => session.set("randomNumber", rnd.nextInt()))
    .exec(session => session.set("customerLoggedIn", false))
    .exec(session => session.set("cartTotal", 0.00))
    .exec(addCookie(Cookie("sessionId", rndString(20)).withDomain(DOMAIN)))
    .exec(session => session.set("startTime", System.currentTimeMillis()))

  private val scn = scenario("Recorded Demostore")
    .exec(initSession)
    .exec(CmsPages.homePage)
    .exec(metricWriter.writeResponseTime(client, "Test1", "HomePage"))
    .pause(1)
    .exec(CmsPages.aboutUsPage)
    .pause(1)
    .exec(Catalog.Category.view)
    .pause(1)
    .exec(Catalog.Product.add)
    .pause(1)
    .exec(Checkout.viewCart)
    .pause(1)
    .exec(Catalog.Product.add)
    .pause(1)
    .exec(Checkout.viewCart)
    .pause(1)
    .exec(Checkout.completeCheckout)

  object UserJourneys {
    def minPause = 100.milliseconds

    def maxPause = 500.milliseconds

    def browseStore = {
      exec(initSession)
        .exec(CmsPages.homePage)
        .pause(maxPause)
        .exec(CmsPages.aboutUsPage)
        .pause(minPause, maxPause)
        .repeat(5) {
          exec(Catalog.Category.view)
            .pause(maxPause)
            .exec(Catalog.Product.view)
            .pause(minPause, maxPause)
        }
    }

    def abandonCart = {
      exec(initSession)
        .exec(CmsPages.homePage)
        .pause(maxPause)
        .exec(Catalog.Category.view)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.view)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.add)
    }

    def completePurchase = {
      exec(initSession)
        .exec(CmsPages.homePage)
        .pause(maxPause)
        .exec(Catalog.Category.view)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.view)
        .pause(minPause, maxPause)
        .exec(Catalog.Product.add)
        .pause(minPause, maxPause)
        .exec(Checkout.viewCart)
        .pause(minPause, maxPause)
        .exec(Checkout.completeCheckout)
    }
  }

  object Scenarios {


    def highPurchase = scenario("High Purchase Scenario")
      .during(testDuration) {
        randomSwitch(
          30d -> exec(UserJourneys.browseStore),
          30d -> exec(UserJourneys.abandonCart),
          40d -> exec(UserJourneys.completePurchase)
        )
      }

    def default = scenario("Default Load Test")
      .during(testDuration) {
        randomSwitch(
          75d -> exec(UserJourneys.browseStore),
          15d -> exec(UserJourneys.abandonCart),
          10d -> exec(UserJourneys.completePurchase)
        )
      }
  }

  // Parallel or consecutive scenarios

  setUp(Scenarios.default
    .inject(rampUsers(userCount).during(rampDuration))
    .protocols(httpProtocol)
    .andThen(Scenarios.highPurchase // remove andThen and paste comma for parallel simulation
      .inject(rampUsers(userCount).during(rampDuration))
      .protocols(httpProtocol))
  ).assertions(
    global.responseTime.max.lt(3),
    global.successfulRequests.percent.gt(99)
  )

  //  Constant User Per Second
  //  setUp(
  //    scn.inject(
  //      constantUsersPerSec(1).during(10)
  //    ).protocols(httpProtocol))

  // Open Model Simulation
  //  setUp(scn.inject(
  //      atOnceUsers(3),
  //      nothingFor(5),
  //      rampUsers(5).during(10),
  //      nothingFor(5),
  //      constantUsersPerSec(1).during(20)
  //    ).protocols(httpProtocol)
  //  )

  // Closed Model Simulation
  //  setUp(
  //    scn.inject(
  //      constantConcurrentUsers(10).during(20),
  //      rampConcurrentUsers(10).to(20).during(20)
  //    ).protocols(httpProtocol)
  //  )

  // Throttling Simulation
  //setUp(
  //  scn.inject(
  //    constantUsersPerSec(1).during(90)
  //  ).protocols(httpProtocol)
  //).throttle(
  //  reachRps(10).in(20),
  //  holdFor(20),
  //  jumpToRps(20),
  //  holdFor(20)
  //).maxDuration(90)

}
