package controllerComponent.controllerImpl.moveStrategy

import api.util.ModelRequest
import de.github.dotsandboxes.lib.Move
import fieldComponent.FieldInterface
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object EdgeState extends MoveState:
  override def handle(move: Move, field: FieldInterface): String =
    def downCase(x: Int, y: Int) = fieldSquareCaseHttp("downCase", x, y)
    def upCase(x: Int, y: Int) = fieldSquareCaseHttp("upCase", x, y)
    def rightCase(x: Int, y: Int) = fieldSquareCaseHttp("rightCase", x, y)
    def leftCase(x: Int, y: Int) = fieldSquareCaseHttp("leftCase", x, y)

    (move.vec, move.x, move.y) match
      case (1, 0, _)                                    => downCase(move.x, move.y)
      case (1, x, _) if x == fieldMaxPosHttp("maxPosX") => upCase(move.x, move.y)
      case (2, _, 0)                                    => rightCase(move.x, move.y)
      case (2, _, y) if y == fieldMaxPosHttp("maxPosY") => leftCase(move.x, move.y)

  private def fieldSquareCaseHttp(squareCase: String, x: Int, y: Int): String =
    Await.result(ModelRequest.postRequest(s"api/field/checkSquare/edgeState/$squareCase", Json.obj(
      "x" -> Json.toJson(x),
      "y" -> Json.toJson(y)
    )), 5.seconds)

  private def fieldMaxPosHttp(pos: String): Int =
    Await.result(ModelRequest.getRequest(s"api/field/get/$pos"), 5.seconds).toInt
