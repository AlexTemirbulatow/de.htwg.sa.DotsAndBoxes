package controllerComponent.controllerImpl.playerStrategy

import fieldComponent.FieldInterface
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import api.util.ModelRequest

object AddTwoPoints extends PlayerState:
  override def handle(field: FieldInterface): String = fieldPlayerPointsHttp

  private def fieldPlayerPointsHttp: String =
    Await.result(ModelRequest.postRequest("api/field/player/add", Json.obj(
      "playerIndex" -> Json.toJson(fieldPlayerIndexHttp),
      "points" -> Json.toJson(2)
    )), 5.seconds)

  private def fieldPlayerIndexHttp: Int =
    Await.result(ModelRequest.getRequest("api/field/get/playerIndex"), 5.seconds).toInt
