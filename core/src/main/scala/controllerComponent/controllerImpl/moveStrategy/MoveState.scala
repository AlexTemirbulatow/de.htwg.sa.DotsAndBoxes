package controllerComponent.controllerImpl.moveStrategy

import de.github.dotsandboxes.lib.Move
import common.model.fieldService.FieldInterface

trait MoveState { def handle(move: Move, field: FieldInterface): String }
