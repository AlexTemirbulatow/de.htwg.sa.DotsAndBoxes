package controllerComponent.controllerImpl.playerStrategy

import api.util.ModelRequest
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object NextPlayer extends PlayerState:
  override def handle(field: String): String = fieldPlayerNextHttp(field)

  private def fieldPlayerNextHttp(field: String): String =
    Await.result(ModelRequest.postRequest("api/field/player/next", Json.obj(
      "field" -> field
    )), 5.seconds)
