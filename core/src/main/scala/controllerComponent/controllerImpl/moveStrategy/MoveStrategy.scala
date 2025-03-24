package controllerComponent.controllerImpl.moveStrategy

import de.github.dotsandboxes.lib.Move

object MoveStrategy:
  def executeStrategy(position: MoveState, move: Move, field: String): String =
    position.handle(move, field)
