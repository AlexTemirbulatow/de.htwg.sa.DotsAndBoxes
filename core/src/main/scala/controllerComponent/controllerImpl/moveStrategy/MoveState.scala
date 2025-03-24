package controllerComponent.controllerImpl.moveStrategy

import de.github.dotsandboxes.lib.Move

trait MoveState { def handle(move: Move, field: String): String }
