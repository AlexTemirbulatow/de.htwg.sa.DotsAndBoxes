package controllerComponent.controllerImpl.command

import api.util.ModelRequest
import de.github.dotsandboxes.lib.Move
import fieldComponent.FieldInterface
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class PutCommand(move: Move, var field: FieldInterface) extends Command:
  override def doStep(field: String): String =
    move.vec match
      case 1 => fieldPlaceHttp("row", move.x, move.y, move.value, field)
      case 2 => fieldPlaceHttp("col", move.x, move.y, move.value, field)
  override def undoStep(field: FieldInterface): FieldInterface =
    val temp = this.field
    this.field = field
    temp
  override def redoStep(field: FieldInterface): FieldInterface =
    val temp = this.field
    this.field = field
    temp

  def fieldPlaceHttp(pos: String, x: Int, y: Int, value: Boolean, field: String): String =
    Await.result(ModelRequest.postRequest(s"api/field/place/$pos", Json.obj(
      "x" -> Json.toJson(x),
      "y" -> Json.toJson(y),
      "value" -> Json.toJson(value),
      "field" -> Json.toJson(field)
    )), 5.seconds)
