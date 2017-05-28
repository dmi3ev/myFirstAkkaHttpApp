package ru.susanin.ivan.models

case class Firm(id: String, hash: String, name: String, cityName: String, reviewsCount: Option[Long])
