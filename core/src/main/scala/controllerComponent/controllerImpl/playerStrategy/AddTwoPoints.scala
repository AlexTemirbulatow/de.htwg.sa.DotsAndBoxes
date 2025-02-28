package controllerComponent.controllerImpl.playerStrategy

import fieldComponent.FieldInterface

object AddTwoPoints extends PlayerState:
  override def handle(field: FieldInterface): FieldInterface =
    field.addPoints(field.playerIndex, 2).updatePlayer(field.playerIndex)
