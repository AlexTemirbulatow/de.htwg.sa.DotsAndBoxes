package de.htwg.se.dotsandboxes.model.computerComponent.computerEasyImpl

import de.htwg.se.dotsandboxes.model.computerComponent.ComputerInterface
import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface
import de.htwg.se.dotsandboxes.util.Move
import scala.util.Random.shuffle

class ComputerEasy extends ComputerInterface:
  override def calculateMove(field: FieldInterface): Option[Move] =
    val allAvailableCoords: Vector[(Int, Int, Int)] =
      field.getUnoccupiedRowCoord() ++ field.getUnoccupiedColCoord()
    if allAvailableCoords.isEmpty then return None

    val winningMoves: Vector[Move] = allAvailableCoords.collect {
      case (vec, x, y) if isClosingMove(field, vec, x, y) => Move(vec, x, y, true)
    }

    winningMoves.headOption.orElse {
      shuffle(allAvailableCoords).head match
        case (vec, x, y) => Some(Move(vec, x, y, true))
    }
