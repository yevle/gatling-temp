package eshop.utils

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object SlackNotificator {
  private val slackMessage = """{ "message": "Gatling test has completed!" }"""
  private val slackDomain = "https://hooks.slack.com/triggers/"
  private val channelId = "T4E815KGA/6273744595475/f6ca30500eb4b1d2761fcccc20f7d99b"
  def sendSlackNotification(): ChainBuilder = doIf(session => {
    session("statusCode").as[Int] != 200
  }) {
    exec(http("slack notification")
      .post(slackDomain + channelId)
      .body(StringBody(slackMessage)).asJson
    )
  }
}