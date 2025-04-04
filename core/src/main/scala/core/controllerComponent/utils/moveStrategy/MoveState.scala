package core.controllerComponent.utils.moveStrategy

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.Move

trait MoveState { def handle(move: Move, field: FieldInterface): String }
