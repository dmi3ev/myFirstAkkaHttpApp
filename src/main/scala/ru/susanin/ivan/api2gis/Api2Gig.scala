package ru.susanin.ivan.api2gis

import akka.actor.{ Actor, ActorLogging, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpEntity, _ }
import akka.pattern.pipe
import ru.susanin.ivan.SusaninActorSystem
import spray.json.JsObject

object Api2Gig {

  case class Request(httpRequest: HttpRequest)

  case class Response(json: JsObject)

  case class Api2GisError(message: String) extends RuntimeException(message)

  case object EmptyResult extends RuntimeException

  def props: Props = Props(new Api2Gig)
}

class Api2Gig extends Actor with ActorLogging {

  import Api2Gig._
  import spray.json._

  private implicit val executionContext = SusaninActorSystem.executionContext
  private implicit val materializer = SusaninActorSystem.materializer
  private implicit val errorResultFormat = Api2GisJsonProtocol.errorResultFormat

  private val http = Http(SusaninActorSystem.system)

  override def receive: Receive = {
    case Request(httpRequest) =>
      log.debug(httpRequest.uri.toString)
      val resp = http.singleRequest(httpRequest)
      resp flatMap {
        case HttpResponse(StatusCodes.OK, _, entity@HttpEntity.Chunked(ContentTypes.`application/json`, _), _) =>
          val data = entity.dataBytes.map(_.utf8String).runReduce(_ + _)
          data.map { string =>
            val jsonObject = string.parseJson.asJsObject
            jsonObject.getFields("response_code") match {
              case Seq(JsString("200")) =>
                jsonObject
              case Seq(JsString("404")) =>
                throw EmptyResult
              case Seq(JsString(_)) =>
                val error = jsonObject.convertTo[ErrorResponse]
                log.error(error.toString)
                throw Api2GisError(error.error_message)
              case _ =>
                log.error(string)
                throw Api2GisError("Unexpected error")
            }
          }
        case response: HttpResponse =>
          log.error(response.toString)
          throw Api2GisError("Unexpected error")
      } pipeTo sender
  }
}
