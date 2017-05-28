package ru.susanin.ivan.api2gis

import spray.json.DefaultJsonProtocol

object Api2GisJsonProtocol extends DefaultJsonProtocol {
  implicit val errorResultFormat = jsonFormat(ErrorResponse.apply, "response_code", "error_code", "error_message")
  implicit val catalogResultFormat = jsonFormat(CatalogItem.apply, "id", "hash", "name", "city_name", "reviews_count")
  implicit val catalogResponseFormat = jsonFormat(CatalogResponse.apply, "total", "result")
  implicit val profileResponseFormat = jsonFormat(ProfileResponse.apply, "id", "address", "rating")
}
