package core
package controllerComponent.moveStrategy

import model.fieldComponent.FieldInterface

object MoveStrategy:
  def executeStrategy(position: MoveState, move: Move, field: FieldInterface): FieldInterface =
    position.handle(move, field)
