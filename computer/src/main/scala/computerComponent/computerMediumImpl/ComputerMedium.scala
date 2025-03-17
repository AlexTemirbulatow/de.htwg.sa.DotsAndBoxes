package computerComponent.computerMediumImpl

import scala.util.Random.shuffle
import computerComponent.ComputerInterface
import fieldComponent.FieldInterface
import de.github.dotsandboxes.lib.Move

class ComputerMedium extends ComputerInterface:
  override def calculateMove(field: FieldInterface): Option[Move] =
    val allAvailableCoords: Vector[(Int, Int, Int)] =
      field.getUnoccupiedRowCoord() ++ field.getUnoccupiedColCoord()
    if allAvailableCoords.isEmpty then return None

    val fallbackMove: Option[Move] = shuffle(allAvailableCoords).head match
      case (vec, x, y) => Some(Move(vec, x, y, true))

    val winningMoves: Vector[Move] = allAvailableCoords.collect {
      case (vec, x, y) if isClosingMove(field, vec, x, y) => Move(vec, x, y, true)
    }
    if winningMoves.nonEmpty then return Some(winningMoves.head)

    val saveMoves: Vector[Move] = allAvailableCoords.collect {
      case (vec, x, y) if !isRiskyMove(field, vec, x, y) => Move(vec, x, y, true)
    }
    if saveMoves.nonEmpty then return Some(shuffle(saveMoves).head)

    return allAvailableCoords
      .map(evaluateChainWithPointsOutcome(_, field))
      .filterNot(_._1 == 0)
      .minByOption(_._1)
      .map(chain => chain._2.head)
      .map(coord => Move(coord._1, coord._2, coord._3, true))
      .orElse(fallbackMove)
