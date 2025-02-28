package controllerComponent.controllerImpl.moveStrategy

import fieldComponent.FieldInterface
import lib.Move

object MoveStrategy:
  def executeStrategy(position: MoveState, move: Move, field: FieldInterface): FieldInterface =
    position.handle(move, field)
