package de.htwg.se.dotsandboxes
package util

import model.fieldComponent.FieldInterface
import moveState.MoveState

/* strategy pattern */
object MoveStratagy:
  def executeStrategy(position: MoveState, move: Move, field: FieldInterface): FieldInterface =
    position.handle(move, field)
