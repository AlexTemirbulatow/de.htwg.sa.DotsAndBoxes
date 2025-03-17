package controllerComponent.controllerImpl.moveStrategy

import fieldComponent.FieldInterface
import de.github.dotsandboxes.lib.{SquareCase, Move}

object EdgeState extends MoveState:
  override def handle(move: Move, field: FieldInterface): FieldInterface =
    def downCase(x: Int, y: Int) = field.checkSquare(SquareCase.DownCase, x, y)
    def upCase(x: Int, y: Int) = field.checkSquare(SquareCase.UpCase, x, y)
    def rightCase(x: Int, y: Int) = field.checkSquare(SquareCase.RightCase, x, y)
    def leftCase(x: Int, y: Int) = field.checkSquare(SquareCase.LeftCase, x, y)

    (move.vec, move.x, move.y) match
      case (1, 0, _)                       => downCase(move.x, move.y)
      case (1, x, _) if x == field.maxPosX => upCase(move.x, move.y)
      case (2, _, 0)                       => rightCase(move.x, move.y)
      case (2, _, y) if y == field.maxPosY => leftCase(move.x, move.y)
