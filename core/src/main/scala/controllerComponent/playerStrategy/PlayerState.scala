package core
package controllerComponent.playerStrategy

import model.fieldComponent.FieldInterface

trait PlayerState { def handle(field: FieldInterface): FieldInterface }
