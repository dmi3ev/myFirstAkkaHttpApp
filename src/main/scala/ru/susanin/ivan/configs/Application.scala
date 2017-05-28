package ru.susanin.ivan.configs

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object Application {

  case class ApplicationConfig(cities: Seq[String])

  private val applicationConfig = ConfigFactory.load("susanin")

  val config: ApplicationConfig = {
    val config = applicationConfig.getConfig("ApplicationConfig")
    ApplicationConfig(
      cities = config.getStringList("cities").asScala
    )
  }

}
