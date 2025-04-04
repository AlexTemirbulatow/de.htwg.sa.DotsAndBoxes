package computer.computerComponent.computerHardImpl

import computer.api.service.ModelRequestHttp
import computer.computerComponent.ComputerInterface
import de.github.dotsandboxes.lib.Move
import scala.util.Random.shuffle

class ComputerHard extends ComputerInterface:
  override def calculateMove(field: String): Option[Move] =
    val allAvailableCoords: Vector[(Int, Int, Int)] = ModelRequestHttp.allAvailableCoords(field)
    if allAvailableCoords.isEmpty then return None

    val winningMoves: Vector[Move] = ModelRequestHttp.winningMoves(field, allAvailableCoords)

    val saveMoves: Vector[Move] = ModelRequestHttp.saveMoves(field, allAvailableCoords)

    if winningMoves.nonEmpty && saveMoves.nonEmpty then return Some(winningMoves.head)
    if winningMoves.isEmpty && saveMoves.nonEmpty then return Some(shuffle(saveMoves).head)

    if winningMoves.nonEmpty && saveMoves.isEmpty then
      val winningChainSequence: Vector[(Int, Vector[(Int, Int, Int)])] =
        ModelRequestHttp.chainsWithPointsOutcome(field, winningMoves.map(move => (move.vec, move.x, move.y)))

      val winningChainInitial: Vector[(Int, (Int, Int, Int))] = winningChainSequence.flatMap {
        case (points, sequence) => sequence.headOption.map(initialMove => (points, initialMove))
      }

      if winningChainSequence.exists(_._1 == 1) then
        return winningChainInitial.collectFirst { case (1, (vec, x, y)) => Move(vec, x, y, true) }

      val remainingChainPoints: Vector[Int] = ModelRequestHttp.chainsWithPointsOutcome(
        field,
        allAvailableCoords.filterNot(moves => winningChainSequence.exists { case (_, chainedMoves) => chainedMoves.contains(moves) })
      ).map(_._1)

      if remainingChainPoints.isEmpty then
        return Some(Move(winningChainInitial.head._2._1, winningChainInitial.head._2._2, winningChainInitial.head._2._3, true))

      if winningChainSequence.size == 2 && ModelRequestHttp.isCircularSequence(field, winningChainSequence(0), winningChainSequence(1)) then
        val moves: Vector[(Int, Int, Int)] = winningChainSequence.head._2
        val chainPoints: Int = winningChainSequence.head._1
        return if chainPoints == 4 then
          if remainingChainPoints.exists(_ <= 4) then
            Some(Move(moves.head._1, moves.head._2, moves.head._3, true))
          else Some(Move(moves(1)._1, moves(1)._2, moves(1)._3, true))
        else Some(Move(moves.head._1, moves.head._2, moves.head._3, true))

      if winningChainSequence.size > 1 then
        return Some(Move(winningChainInitial.head._2._1, winningChainInitial.head._2._2, winningChainInitial.head._2._3, true))
      
      return winningChainSequence.collectFirst {
        case (2, moves) if remainingChainPoints.exists(_ <= 2) => Move(moves.head._1, moves.head._2, moves.head._3, true)
        case (2, moves) => Move(moves.last._1, moves.last._2, moves.last._3, true)
      }.orElse {
        Some(Move(winningChainInitial.head._2._1, winningChainInitial.head._2._2, winningChainInitial.head._2._3, true))
      }
    else
      val riskyChainSequence: Vector[(Int, Vector[(Int, Int, Int)])] =
        ModelRequestHttp.chainsWithPointsOutcome(field, allAvailableCoords)

      val riskyChainInitial: Vector[(Int, (Int, Int, Int))] = riskyChainSequence.flatMap {
        case (points, sequence) => sequence.headOption.map(initialMove => (points, initialMove))
      }

      return riskyChainInitial
        .sortBy(_._1)
        .collectFirst {
          case (1, (vec, x, y)) => Move(vec, x, y, true)
          case (2, (vec, x, y)) if ModelRequestHttp.missingMoves(field, vec, x, y).size == 2 => Move(vec, x, y, true)
        }.orElse {
          riskyChainInitial
            .minByOption(_._1)
            .map { case (_, (vec, x, y)) => Move(vec, x, y, true) }
        }
