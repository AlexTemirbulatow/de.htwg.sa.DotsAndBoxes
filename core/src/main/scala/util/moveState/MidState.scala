package core
package util.moveState

import model.fieldComponent.FieldInterface
import util.Move

object MidState extends MoveState:
  override def handle(move: Move, field: FieldInterface): FieldInterface =
    def horizontalState(move: Move): FieldInterface = field.checkSquare(SquareState.DownCase, move.x, move.y).checkSquare(SquareState.UpCase, move.x, move.y)
    def verticalState(move: Move): FieldInterface = field.checkSquare(SquareState.RightCase, move.x, move.y).checkSquare(SquareState.LeftCase, move.x, move.y)

    move.vec match
      case 1 => horizontalState(move)
      case 2 => verticalState(move)
