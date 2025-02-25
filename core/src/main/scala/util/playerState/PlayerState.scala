package core
package util.playerState

import model.fieldComponent.FieldInterface

trait PlayerState { def handle(field: FieldInterface): FieldInterface }
