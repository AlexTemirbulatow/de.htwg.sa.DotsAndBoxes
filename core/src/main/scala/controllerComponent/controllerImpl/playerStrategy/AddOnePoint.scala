package controllerComponent.controllerImpl.playerStrategy

import fieldComponent.FieldInterface

object AddOnePoint extends PlayerState:
  override def handle(field: FieldInterface): FieldInterface =
    field.addPoints(field.playerIndex, 1).updatePlayer(field.playerIndex)
