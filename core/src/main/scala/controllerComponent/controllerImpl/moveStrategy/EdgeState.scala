package controllerComponent.controllerImpl.moveStrategy

import api.util.ModelRequest
import de.github.dotsandboxes.lib.Move
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object EdgeState extends MoveState:
  override def handle(move: Move, field: String): String =
    def downCase(x: Int, y: Int) = fieldSquareCaseHttp("downCase", x, y, field)
    def upCase(x: Int, y: Int) = fieldSquareCaseHttp("upCase", x, y, field)
    def rightCase(x: Int, y: Int) = fieldSquareCaseHttp("rightCase", x, y, field)
    def leftCase(x: Int, y: Int) = fieldSquareCaseHttp("leftCase", x, y, field)

    (move.vec, move.x, move.y) match
      case (1, 0, _)                                           => downCase(move.x, move.y)
      case (1, x, _) if x == fieldMaxPosHttp("maxPosX", field) => upCase(move.x, move.y)
      case (2, _, 0)                                           => rightCase(move.x, move.y)
      case (2, _, y) if y == fieldMaxPosHttp("maxPosY", field) => leftCase(move.x, move.y)

  private def fieldSquareCaseHttp(squareCase: String, x: Int, y: Int, field: String): String =
    Await.result(ModelRequest.postRequest(s"api/field/checkSquare/edgeState/$squareCase", Json.obj(
      "x" -> Json.toJson(x),
      "y" -> Json.toJson(y),
      "field" -> Json.toJson(field)
    )), 5.seconds)

  private def fieldMaxPosHttp(pos: String, field: String): Int =
    Await.result(ModelRequest.postRequest(s"api/field/get/$pos", Json.obj(
      "field" -> Json.toJson(field)
    )), 5.seconds).toInt
