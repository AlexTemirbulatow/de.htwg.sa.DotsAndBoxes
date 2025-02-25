package core
package util.playerState

import model.fieldComponent.FieldInterface

object AddOnePoint extends PlayerState:
  override def handle(field: FieldInterface): FieldInterface =
    field.addPoints(field.playerIndex, 1).updatePlayer(field.playerIndex)
