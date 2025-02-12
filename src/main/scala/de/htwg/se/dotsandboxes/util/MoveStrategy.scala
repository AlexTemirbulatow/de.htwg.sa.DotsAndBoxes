package de.htwg.se.dotsandboxes
package util

import model.fieldComponent.FieldInterface
import moveState.MoveState

object MoveStrategy:
  def executeStrategy(position: MoveState, move: Move, field: FieldInterface): FieldInterface =
    position.handle(move, field)
