package computerComponent.computerEasyImpl

import scala.util.Random.shuffle
import computerComponent.ComputerInterface
import fieldComponent.FieldInterface
import lib.Move

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
