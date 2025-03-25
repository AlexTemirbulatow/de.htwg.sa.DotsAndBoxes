package controllerComponent.controllerImpl.moveStrategy

import de.github.dotsandboxes.lib.Move
import fieldComponent.FieldInterface

trait MoveState { def handle(move: Move, field: FieldInterface): String }
