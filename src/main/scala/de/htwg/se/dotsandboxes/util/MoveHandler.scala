package de.htwg.se.dotsandboxes
package util

import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Move
import scala.util.{Failure, Success, Try}

/* chain of responsibility */
trait MoveHandler:
  val next: Option[MoveHandler]
  def handle(move: Move, field: FieldInterface): Try[String]

class CheckLine(val next: Option[MoveHandler]) extends MoveHandler:
  override def handle(move: Move, field: FieldInterface): Try[String] =
    (move.vec > 0 && move.vec < 3) match
      case false => Failure(new MatchError("\n<Line> index failed the check. Try again: "))
      case true =>
        next match
          case Some(nextHandler: MoveHandler) => nextHandler.handle(move, field)
          case None                           => Failure(new Exception("could not handle."))

class CheckX(val next: Option[MoveHandler]) extends MoveHandler:
  override def handle(move: Move, field: FieldInterface): Try[String] =
    (move.x >= 0 && move.x <= field.maxPosX) match
      case false => Failure(new MatchError("\n<X> coordinate failed the check. Try again: "))
      case true =>
        next match
          case Some(nextHandler: MoveHandler) => nextHandler.handle(move, field)
          case None                           => Failure(new Exception("could not handle."))

class CheckY(val next: Option[MoveHandler]) extends MoveHandler:
  override def handle(move: Move, field: FieldInterface): Try[String] =
    (move.y >= 0 && move.y <= field.maxPosY) match
      case false => Failure(new MatchError("\n<Y> coordinate failed the check. Try again: "))
      case true =>
        next match
          case Some(nextHandler: MoveHandler) => nextHandler.handle(move, field)
          case None                           => Failure(new Exception("could not handle."))

class CheckAvailable(val next: Option[MoveHandler]) extends MoveHandler:
  override def handle(move: Move, field: FieldInterface): Try[String] =
    val isTaken = move.vec match
      case 1 => field.getRowCell(move.x, move.y)
      case 2 => field.getColCell(move.x, move.y)
    isTaken match
      case true => Failure(new MatchError("\nThis line is already taken. Try again: "))
      case false =>
        next match
          case Some(nextHandler: MoveHandler) => nextHandler.handle(move, field)
          case None                           => Success("Move was successful!")
