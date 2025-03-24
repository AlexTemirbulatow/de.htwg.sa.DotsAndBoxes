package controllerComponent.controllerImpl.moveStrategy

import api.util.ModelRequest
import de.github.dotsandboxes.lib.Move
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object MidState extends MoveState:
  override def handle(move: Move, field: String): String =
    def horizontalState(move: Move): String = fieldSquareCaseHttp("horizontal", move.x, move.y, field)
    def verticalState(move: Move): String = fieldSquareCaseHttp("vertical", move.x, move.y, field)

    move.vec match
      case 1 => horizontalState(move)
      case 2 => verticalState(move)

  private def fieldSquareCaseHttp(state: String, x: Int, y: Int, field: String): String =
    Await.result(ModelRequest.postRequest(s"api/field/checkSquare/midState/$state", Json.obj(
      "x" -> Json.toJson(x),
      "y" -> Json.toJson(y),
      "field" -> Json.toJson(field)
    )), 5.seconds)
