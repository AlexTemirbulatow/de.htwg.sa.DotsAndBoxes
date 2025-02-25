package core
package util.moveState

import model.fieldComponent.FieldInterface
import util.Move

trait MoveState { def handle(move: Move, field: FieldInterface): FieldInterface }
