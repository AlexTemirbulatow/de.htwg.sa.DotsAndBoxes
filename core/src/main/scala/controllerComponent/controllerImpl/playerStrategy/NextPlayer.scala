package controllerComponent.controllerImpl.playerStrategy

import fieldComponent.FieldInterface

object NextPlayer extends PlayerState:
  override def handle(field: FieldInterface): FieldInterface = field.nextPlayer
