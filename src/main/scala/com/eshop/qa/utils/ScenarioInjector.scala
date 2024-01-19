package com.eshop.qa.utils

import io.gatling.core.Predef._
import io.gatling.core.controller.inject.closed.ClosedInjectionStep
import io.gatling.core.controller.inject.open.OpenInjectionStep

import scala.concurrent.duration.FiniteDuration

object ScenarioInjector {
  def injectComplexOpenModel (usersRatePerSecond: Int, rampUpDurationSeconds: FiniteDuration, steadyStateDurationSeconds: FiniteDuration)
    : Seq[OpenInjectionStep] = {
    Seq.apply(
      rampUsersPerSec(0) to usersRatePerSecond during rampUpDurationSeconds,
      constantUsersPerSec(usersRatePerSecond) during steadyStateDurationSeconds
    )
  }

  def injectOpenModel(userCount: Int, rampUpDuration: FiniteDuration)
  : Seq[OpenInjectionStep] = {
    Seq.apply(
      rampUsers(userCount) during rampUpDuration
    )
  }

  def injectClosedModel (concurrentUsersAmount: Int, rampUpDurationSeconds: FiniteDuration, steadyStateDurationSeconds: FiniteDuration): Seq[ClosedInjectionStep] = {
    Seq.apply(
      rampConcurrentUsers(0) to concurrentUsersAmount during rampUpDurationSeconds,
      constantConcurrentUsers(concurrentUsersAmount) during steadyStateDurationSeconds
    )
  }
}