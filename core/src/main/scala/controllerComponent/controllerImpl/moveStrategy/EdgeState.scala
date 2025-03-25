package controllerComponent.controllerImpl.moveStrategy

import api.utils.ModelRequestHttp
import de.github.dotsandboxes.lib.Move
import fieldComponent.FieldInterface

object EdgeState extends MoveState:
  override def handle(move: Move, field: FieldInterface): String =
    def downCase(x: Int, y: Int) = ModelRequestHttp.squareCase("downCase", x, y, field)
    def upCase(x: Int, y: Int) = ModelRequestHttp.squareCase("upCase", x, y, field)
    def rightCase(x: Int, y: Int) = ModelRequestHttp.squareCase("rightCase", x, y, field)
    def leftCase(x: Int, y: Int) = ModelRequestHttp.squareCase("leftCase", x, y, field)

    (move.vec, move.x, move.y) match
      case (1, 0, _)                                         => downCase(move.x, move.y)
      case (1, x, _) if x == ModelRequestHttp.maxPosX(field) => upCase(move.x, move.y)
      case (2, _, 0)                                         => rightCase(move.x, move.y)
      case (2, _, y) if y == ModelRequestHttp.maxPosY(field) => leftCase(move.x, move.y)
