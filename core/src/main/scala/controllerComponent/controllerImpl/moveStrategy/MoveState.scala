package controllerComponent.controllerImpl.moveStrategy

import fieldComponent.FieldInterface
import lib.Move

trait MoveState { def handle(move: Move, field: FieldInterface): FieldInterface }
