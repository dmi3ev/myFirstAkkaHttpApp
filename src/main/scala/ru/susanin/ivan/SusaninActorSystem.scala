package ru.susanin.ivan

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ru.susanin.ivan.configs.Api2Gis

object SusaninActorSystem {
  implicit val system = ActorSystem("ivan-susanin")
  val materializer = ActorMaterializer()
  val executionContext = system.dispatcher
  val timeout = Timeout(Api2Gis.config.timeout)
}
