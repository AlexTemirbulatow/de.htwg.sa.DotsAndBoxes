package de.htwg.se.dotsandboxes.util

class PackT[T](val moves: List[T]):
  def map(func: T => T): List[T] = moves.map(move => func(move))