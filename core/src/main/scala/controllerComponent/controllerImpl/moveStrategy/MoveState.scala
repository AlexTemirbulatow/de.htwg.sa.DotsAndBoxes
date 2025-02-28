package core
package controllerComponent.moveStrategy

import model.fieldComponent.FieldInterface
import lib.Move

trait MoveState { def handle(move: Move, field: FieldInterface): FieldInterface }
