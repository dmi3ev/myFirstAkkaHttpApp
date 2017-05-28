package ru.susanin.ivan

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import ru.susanin.ivan.models.PathItem
import spray.json.DefaultJsonProtocol

import scala.io.StdIn

object WebServer extends App with SprayJsonSupport with DefaultJsonProtocol {
  implicit val system = SusaninActorSystem.system
  implicit val materializer = SusaninActorSystem.materializer
  implicit val executionContext = SusaninActorSystem.executionContext
  implicit val firmPathFormat = jsonFormat3(PathItem)

  val route =
    pathSingleSlash {
      parameter("search") {
        case short: String if short.length < 2 =>
          complete(StatusCodes.BadRequest)
        case search =>
          complete(SusaninPath.get(search))
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nTry http://localhost:8080/?search=2gis\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
