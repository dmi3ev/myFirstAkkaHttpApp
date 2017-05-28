package ru.susanin.ivan.configs

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

object Api2Gis {
  implicit def toFiniteDuration(d: java.time.Duration): FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  case class ApiConfig(key: String, version: String, scheme: String, host: String, paths: Paths, timeout: FiniteDuration)

  case class Paths(searchFirm: String, searchProfile: String)

  private val api2gisConfig = ConfigFactory.load("api2gis")

  val config: ApiConfig = {
    val config = api2gisConfig.getConfig("ApiConfig")
    val paths = config.getConfig("paths")
    ApiConfig(
      key = config.getString("key"),
      version = config.getString("version"),
      scheme = config.getString("scheme"),
      host = config.getString("host"),
      paths = Paths(
        searchFirm = paths.getString("searchFirm"),
        searchProfile = paths.getString("searchProfile")
      ),
      timeout = config.getDuration("timeout")
    )
  }

}
