package controllerComponent.controllerImpl.playerStrategy

import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import api.util.ModelRequest

object AddOnePoint extends PlayerState:
  override def handle(field: String): String = fieldPlayerPointsHttp(field)

  private def fieldPlayerPointsHttp(field: String): String =
    Await.result(ModelRequest.postRequest("api/field/player/add", Json.obj(
      "playerIndex" -> Json.toJson(fieldPlayerIndexHttp(field)),
      "points" -> Json.toJson(1),
      "field" -> Json.toJson(field)
    )), 5.seconds)

  private def fieldPlayerIndexHttp(field: String): Int =
    Await.result(ModelRequest.postRequest("api/field/get/playerIndex", Json.obj(
      "field" -> Json.toJson(field)
    )), 5.seconds).toInt
