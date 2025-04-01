package computer.computerComponent.computerMediumImpl

import computer.api.service.ModelRequestHttp
import computer.computerComponent.ComputerInterface
import de.github.dotsandboxes.lib.Move
import scala.util.Random.shuffle

class ComputerMedium extends ComputerInterface:
  override def calculateMove(field: String): Option[Move] =
    val allAvailableCoords: Vector[(Int, Int, Int)] = ModelRequestHttp.allAvailableCoords(field)
    if allAvailableCoords.isEmpty then return None

    val fallbackMove: Option[Move] = shuffle(allAvailableCoords).head match
      case (vec, x, y) => Some(Move(vec, x, y, true))

    val winningMoves: Vector[Move] = ModelRequestHttp.winningMoves(field, allAvailableCoords)
    if winningMoves.nonEmpty then return Some(winningMoves.head)

    val saveMoves: Vector[Move] = ModelRequestHttp.saveMoves(field, allAvailableCoords)
    if saveMoves.nonEmpty then return Some(shuffle(saveMoves).head)

    return ModelRequestHttp
      .chainsWithPointsOutcome(field, allAvailableCoords)
      .filterNot(_._1 == 0)
      .minByOption(_._1)
      .map(chain => chain._2.head)
      .map(coord => Move(coord._1, coord._2, coord._3, true))
      .orElse(fallbackMove)
