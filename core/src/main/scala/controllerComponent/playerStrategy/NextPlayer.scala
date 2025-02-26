package core
package controllerComponent.playerStrategy

import model.fieldComponent.FieldInterface

object NextPlayer extends PlayerState:
  override def handle(field: FieldInterface): FieldInterface = field.nextPlayer
