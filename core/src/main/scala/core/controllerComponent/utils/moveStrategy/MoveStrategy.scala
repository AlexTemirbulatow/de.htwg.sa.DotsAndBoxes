package core.controllerComponent.utils.moveStrategy

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.Move

object MoveStrategy:
  def executeStrategy(position: MoveState, move: Move, field: FieldInterface): String =
    position.handle(move, field)
