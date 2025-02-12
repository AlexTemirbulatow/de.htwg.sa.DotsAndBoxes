package de.htwg.se.dotsandboxes
package util

import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface

case class MoveValidator2(move: Option[Move], field: FieldInterface):
  def validateLine: MoveValidator2 = move match
    case Some(move: Move) =>
      if move.vec > 0 && move.vec < 3 then copy(Some(move))
      else copy(None)
    case None => copy(None)

  def validateXCoord: MoveValidator2 = move match
    case Some(move: Move) =>
      if move.x >= 0 && move.x <= field.maxPosX then copy(Some(move))
      else copy(None)
    case None => copy(None)

  def validateYCoord: MoveValidator2 = move match
    case Some(move: Move) =>
      if move.y >= 0 && move.y <= field.maxPosY then copy(Some(move))
      else copy(None)
    case None => copy(None)

  def validateAvailable: MoveValidator2 = move match
    case Some(move: Move) =>
      val isTaken = move.vec match
        case 1 => field.getRowCell(move.x, move.y)
        case 2 => field.getColCell(move.x, move.y)
      if !isTaken then copy(Some(move))
      else copy(None)
    case None => copy(None)
