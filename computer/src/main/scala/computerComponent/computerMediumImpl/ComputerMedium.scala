package computerComponent.computerMediumImpl

import api.service.ModelRequestHttp
import computerComponent.ComputerInterface
import de.github.dotsandboxes.lib.Move
import scala.util.Random.shuffle

class ComputerMedium extends ComputerInterface:
  override def calculateMove(field: String): Option[Move] =
    val allAvailableCoords: Vector[(Int, Int, Int)] = ModelRequestHttp.allAvailableCoords(field)
    if allAvailableCoords.isEmpty then return None

    val fallbackMove: Option[Move] = shuffle(allAvailableCoords).head match
      case (vec, x, y) => Some(Move(vec, x, y, true))

    val winningMoves: Vector[Move] = allAvailableCoords.collect {
      case (vec, x, y) if ModelRequestHttp.isClosingMove(field, vec, x, y) => Move(vec, x, y, true)
    }
    if winningMoves.nonEmpty then return Some(winningMoves.head)

    val saveMoves: Vector[Move] = allAvailableCoords.collect {
      case (vec, x, y) if !ModelRequestHttp.isRiskyMove(field, vec, x, y) => Move(vec, x, y, true)
    }
    if saveMoves.nonEmpty then return Some(shuffle(saveMoves).head)

    return allAvailableCoords
      .map(ModelRequestHttp.evaluateChainWithPointsOutcome(field, _))
      .filterNot(_._1 == 0)
      .minByOption(_._1)
      .map(chain => chain._2.head)
      .map(coord => Move(coord._1, coord._2, coord._3, true))
      .orElse(fallbackMove)
