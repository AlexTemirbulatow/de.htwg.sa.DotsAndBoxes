package computerComponent.computerEasyImpl

import api.service.ModelRequestHttp
import computerComponent.ComputerInterface
import de.github.dotsandboxes.lib.Move
import scala.util.Random.shuffle

class ComputerEasy extends ComputerInterface:
  override def calculateMove(field: String): Option[Move] =
    val allAvailableCoords: Vector[(Int, Int, Int)] = ModelRequestHttp.allAvailableCoords(field)
    if allAvailableCoords.isEmpty then return None

    val winningMoves: Vector[Move] = ModelRequestHttp.winningMoves(field, allAvailableCoords)

    winningMoves.headOption.orElse {
      shuffle(allAvailableCoords).head match
        case (vec, x, y) => Some(Move(vec, x, y, true))
    }
