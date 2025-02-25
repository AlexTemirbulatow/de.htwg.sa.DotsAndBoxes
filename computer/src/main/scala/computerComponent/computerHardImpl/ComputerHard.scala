package computer
package computerHardImpl

import de.htwg.se.dotsandboxes.model.computerComponent.ComputerInterface
import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface
import de.htwg.se.dotsandboxes.util.Move
import scala.util.Random.shuffle

class ComputerHard extends ComputerInterface:
  override def calculateMove(field: FieldInterface): Option[Move] =
    val allAvailableCoords: Vector[(Int, Int, Int)] =
      field.getUnoccupiedRowCoord() ++ field.getUnoccupiedColCoord()
    if allAvailableCoords.isEmpty then return None

    val winningMoves: Vector[Move] = allAvailableCoords.collect {
      case (vec, x, y) if isClosingMove(field, vec, x, y) => Move(vec, x, y, true)
    }
    
    val saveMoves: Vector[Move] = allAvailableCoords.collect {
      case (vec, x, y) if !winningMoves.contains(Move(vec, x, y, true)) && !isRiskyMove(field, vec, x, y) => Move(vec, x, y, true)
    }

    if winningMoves.nonEmpty && saveMoves.nonEmpty then return Some(winningMoves.head)
    if winningMoves.isEmpty && saveMoves.nonEmpty then return Some(shuffle(saveMoves).head)

    if winningMoves.nonEmpty && saveMoves.isEmpty then
      val winningChainSequence: Vector[(Int, Vector[(Int, Int, Int)])] =
        winningMoves.map(move => evaluateChainWithPointsOutcome((move.vec, move.x, move.y), field))

      val winningChainInitial: Vector[(Int, (Int, Int, Int))] = winningChainSequence.flatMap {
        case (points, sequence) => sequence.headOption.map(initialMove => (points, initialMove))
      }

      if winningChainSequence.exists(_._1 == 1) then
        return winningChainInitial.collectFirst { case (1, (vec, x, y)) => Move(vec, x, y, true) }

      val remainingChainPoints: Vector[Int] = allAvailableCoords
        .filterNot(moves => winningChainSequence.exists { case (_, chainedMoves) => chainedMoves.contains(moves) })
        .map(coord => evaluateChainWithPointsOutcome(coord, field)._1)

      if remainingChainPoints.isEmpty then
        return Some(Move(winningChainInitial.head._2._1, winningChainInitial.head._2._2, winningChainInitial.head._2._3, true))

      if winningChainSequence.size == 2 && isCircularSequence(winningChainSequence(0), winningChainSequence(1)) then
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
        allAvailableCoords.map(coord => evaluateChainWithPointsOutcome(coord, field))

      val riskyChainInitial: Vector[(Int, (Int, Int, Int))] = riskyChainSequence.flatMap {
        case (points, sequence) => sequence.headOption.map(initialMove => (points, initialMove))
      }

      return riskyChainInitial
        .sortBy(_._1)
        .collectFirst {
          case (1, (vec, x, y)) => Move(vec, x, y, true)
          case (2, (vec, x, y)) if getMissingMoves(field, vec, x, y).size == 2 => Move(vec, x, y, true)
        }.orElse {
          riskyChainInitial
            .minByOption(_._1)
            .map { case (_, (vec, x, y)) => Move(vec, x, y, true) }
        }
