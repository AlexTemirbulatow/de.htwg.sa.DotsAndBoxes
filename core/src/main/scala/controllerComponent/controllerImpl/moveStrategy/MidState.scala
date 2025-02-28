package core
package controllerComponent.moveStrategy

import model.fieldComponent.FieldInterface
import lib.Move

object MidState extends MoveState:
  override def handle(move: Move, field: FieldInterface): FieldInterface =
    def horizontalState(move: Move): FieldInterface = field.checkSquare(SquareCases.DownCase, move.x, move.y).checkSquare(SquareCases.UpCase, move.x, move.y)
    def verticalState(move: Move): FieldInterface = field.checkSquare(SquareCases.RightCase, move.x, move.y).checkSquare(SquareCases.LeftCase, move.x, move.y)

    move.vec match
      case 1 => horizontalState(move)
      case 2 => verticalState(move)
