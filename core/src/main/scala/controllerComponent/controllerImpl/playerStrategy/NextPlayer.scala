package controllerComponent.controllerImpl.playerStrategy

import api.util.ModelRequest
import fieldComponent.FieldInterface
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object NextPlayer extends PlayerState:
  override def handle(field: FieldInterface): String = fieldPlayerNextHttp

  private def fieldPlayerNextHttp: String =
    Await.result(ModelRequest.getRequest("api/field/player/next"), 5.seconds)
