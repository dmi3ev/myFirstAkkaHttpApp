package ru.susanin.ivan.api2gis

import akka.actor.{ Actor, ActorLogging, Props }
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import ru.susanin.ivan.SusaninActorSystem
import ru.susanin.ivan.configs.Api2Gis
import ru.susanin.ivan.models.FirmProfile

object Profile {

  case class SearchProfile(id: String, hash: String)

  def props: Props = Props(new Profile)

  private val baseUri = Uri.from(scheme = Api2Gis.config.scheme, host = Api2Gis.config.host,
    path = Api2Gis.config.paths.searchProfile)

  private val defaultQuery = Map("key" -> Api2Gis.config.key, "version" -> Api2Gis.config.version, "output" -> "json")

}

class Profile extends Actor with ActorLogging {

  import Profile._
  import akka.pattern._
  import spray.json._

  private implicit val executionContext = SusaninActorSystem.executionContext
  private implicit val materializer = SusaninActorSystem.materializer
  private implicit val timeout = SusaninActorSystem.timeout

  private implicit val profileResponseFormat = Api2GisJsonProtocol.profileResponseFormat

  private val api2GisActor = SusaninActorSystem.system.actorOf(Api2Gig.props, "api2GisProfileActor")

  override def receive: Receive = {
    case SearchProfile(id, hash) =>
      log.debug(s"start search profile for $id")
      val uri = baseUri withQuery Query(defaultQuery ++ Map("id" -> id, "hash" -> hash))
      (api2GisActor ? Api2Gig.Request(HttpRequest(uri = uri)))
        .mapTo[JsObject]
        .map(_.convertTo[ProfileResponse])
        .map { profile =>
          FirmProfile(id = profile.id, address = profile.address, rating = profile.rating)
        } pipeTo sender
  }

}
