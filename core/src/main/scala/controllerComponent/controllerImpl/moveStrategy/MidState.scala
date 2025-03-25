package controllerComponent.controllerImpl.moveStrategy

import api.util.ModelRequestHttp
import de.github.dotsandboxes.lib.Move
import fieldComponent.FieldInterface

object MidState extends MoveState:
  override def handle(move: Move, field: FieldInterface): String =
    def horizontalState(move: Move): String = ModelRequestHttp.squareState("horizontal", move.x, move.y, field)
    def verticalState(move: Move): String = ModelRequestHttp.squareState("vertical", move.x, move.y, field)

    move.vec match
      case 1 => horizontalState(move)
      case 2 => verticalState(move)
