package controllerComponent.controllerImpl.moveStrategy

import fieldComponent.FieldInterface
import de.github.dotsandboxes.lib.Move

trait MoveState { def handle(move: Move, field: FieldInterface): FieldInterface }
