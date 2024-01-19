package com.eshop.qa.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class SlackNotificationSimulation extends Simulation{
  val httpProtocol = http
    .baseUrl("https://hooks.slack.com") // Your Slack Webhook URL
  val anotherProtocol = http
    .baseUrl("https://hooks.slack.com") // Your Slack Webhook URL

  val slackMessage = """{ "message": "Gatling test has completed!" }"""

  val scn = scenario("Slack Notification")
    .exec(http("Slack Webhook")
      .post("/triggers/T4E815KGA/6273744595475/f6ca30500eb4b1d2761fcccc20f7d99b") // Append your actual webhook path
      .body(StringBody(slackMessage))
      .header("Content-Type", "application/json")
      .check(status.is(200)))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
    .protocols(anotherProtocol)
}