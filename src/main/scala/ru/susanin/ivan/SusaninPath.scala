package ru.susanin.ivan

import ru.susanin.ivan.api2gis.Catalog.SearchCatalog
import ru.susanin.ivan.api2gis.Profile.SearchProfile
import ru.susanin.ivan.api2gis._
import ru.susanin.ivan.configs.Application
import ru.susanin.ivan.models._

import scala.concurrent.Future

object SusaninPath {

  import akka.pattern.ask

  private implicit val executionContext = SusaninActorSystem.executionContext
  private implicit val timeout = SusaninActorSystem.timeout

  private val catalogActor = SusaninActorSystem.system.actorOf(Catalog.props, "catalogActor")
  private val profileActor = SusaninActorSystem.system.actorOf(Profile.props, "profileActor")

  private val cities = Application.config.cities

  def get(what: String): Future[Seq[PathItem]] = Future.sequence {
    cities.map { city =>
      (catalogActor ? SearchCatalog(what, city))
        .mapTo[Option[Firm]]
        .flatMap {
          case Some(firm) =>
            (profileActor ? SearchProfile(firm.id, firm.hash))
              .mapTo[FirmProfile]
              .map { firmProfile =>
                Some(PathItem(firm.name, s"${firm.cityName}, ${firmProfile.address}",
                  firmProfile.rating)) // TODO иногда рейтинг равен "0" .filter(_ != "0")  ?
              }
          case _ => Future.successful {
            None
          }
        }
    }
  }.map {
    _.flatten.sortBy(_.rating.map(_.toDouble).getOrElse(.0))(Ordering.Double.reverse)
  }
}
