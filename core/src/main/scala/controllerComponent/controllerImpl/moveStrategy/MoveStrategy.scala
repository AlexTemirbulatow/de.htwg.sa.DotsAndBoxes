package controllerComponent.controllerImpl.moveStrategy

import de.github.dotsandboxes.lib.Move
import fieldComponent.FieldInterface

object MoveStrategy:
  def executeStrategy(position: MoveState, move: Move, field: FieldInterface): String =
    position.handle(move, field)
