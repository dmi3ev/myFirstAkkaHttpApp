package ru.susanin.ivan.api2gis

import akka.actor.{ Actor, ActorLogging, Props }
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import ru.susanin.ivan.SusaninActorSystem
import ru.susanin.ivan.configs.Api2Gis
import ru.susanin.ivan.models.Firm

object Catalog {

  case class SearchCatalog(what: String, where: String)

  def props: Props = Props(new Catalog)

  private val baseUri = Uri.from(scheme = Api2Gis.config.scheme, host = Api2Gis.config.host,
    path = Api2Gis.config.paths.searchFirm)

  private val defaultQuery = Map("key" -> Api2Gis.config.key, "version" -> Api2Gis.config.version, "sort" -> "rating",
    "page" -> "1", "pagesize" -> "5", "output" -> "json")

}

class Catalog extends Actor with ActorLogging {

  import Catalog._
  import akka.pattern._
  import spray.json._

  private implicit val executionContext = SusaninActorSystem.executionContext
  private implicit val materializer = SusaninActorSystem.materializer
  private implicit val timeout = SusaninActorSystem.timeout

  private implicit val catalogResponseFormat = Api2GisJsonProtocol.catalogResponseFormat

  private val api2GisActor = SusaninActorSystem.system.actorOf(Api2Gig.props, "api2GisCatalogActor")

  override def receive: Receive = {
    case SearchCatalog(what, where) =>
      log.debug(s"start search catalog $what in $where")
      val uri = baseUri withQuery Query(defaultQuery ++ Map("what" -> what, "where" -> where))
      (api2GisActor ? Api2Gig.Request(HttpRequest(uri = uri)))
        .mapTo[JsObject]
        .map(_.convertTo[CatalogResponse])
        .collect {
          case catalog if catalog.result.nonEmpty =>
            val result = catalog.result.maxBy(_.reviews_count.getOrElse(0L))
            Some(Firm(id = result.id, hash = result.hash, name = result.name, cityName = result.city_name,
              reviewsCount = result.reviews_count))
          case _ => None
        }.recover {
          case Api2Gig.EmptyResult =>
            None
      } pipeTo sender
  }

}
