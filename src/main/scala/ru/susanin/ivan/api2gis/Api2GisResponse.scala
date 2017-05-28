package ru.susanin.ivan.api2gis

import ru.susanin.ivan.configs.Api2Gis

sealed trait Api2GisResponse {
  final val api_version: String = Api2Gis.config.version
  val response_code: String
}

case class ErrorResponse(response_code: String, error_code: String, error_message: String) extends Api2GisResponse

trait SuccessResponse extends Api2GisResponse {
  override val response_code: String = "200"
}

case class CatalogItem(id: String, hash: String, name: String, city_name: String, reviews_count: Option[Long])

case class CatalogResponse(total: String, result: Seq[CatalogItem]) extends SuccessResponse

case class ProfileResponse(id: String, address: String, rating: Option[String]) extends SuccessResponse
