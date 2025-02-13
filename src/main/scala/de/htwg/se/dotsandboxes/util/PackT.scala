package de.htwg.se.dotsandboxes.util

class PackT[T](val moves: List[T]):
  def map(func: T => T): List[T] = moves.map(move => func(move))
  override def hashCode(): Int = moves.hashCode()
  override def equals(obj: Any): Boolean = obj match
    case that: PackT[_] => this.moves == that.moves
    case _              => false
