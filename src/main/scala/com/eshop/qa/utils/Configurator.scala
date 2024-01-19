package com.eshop.qa.utils

import com.typesafe.config.ConfigFactory

object Configurator {
  val conf = ConfigFactory.load().withFallback(ConfigFactory.parseResources("application.conf"))

  val url: String = conf.getString("url")
  val rampUpDurationSeconds: Int = conf.getInt("ramp-up-duration-seconds")
  val steadyStateDurationSeconds: Int = conf.getInt("steady-state-duration-seconds")
  val testDurationSeconds: Int = conf.getInt("test-duration-seconds")
  val usersRatePerSecond: Int = conf.getInt("users-rate-per-second")
  val usersCount: Int = conf.getInt("users-count")
  val concurrentUsersAmount: Int = conf.getInt("concurrent-users-amount")
  val paceMilliseconds: Int = conf.getInt("pace-milliseconds")

//  val testDurationMilliseconds = rampUpDurationSeconds.toMillis + steadyStateDurationSeconds.toMillis
}