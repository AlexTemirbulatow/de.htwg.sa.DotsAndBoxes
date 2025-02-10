package de.htwg.se.dotsandboxes
package util

import model.fieldComponent.FieldInterface
import playerState.{AddOnePoint, AddTwoPoints, NextPlayer}
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status

/* strategy pattern */
object PlayerStratagy:
  def updatePlayer(field: FieldInterface, preStatus: Vector[Vector[Status]], postStatus: Vector[Vector[Status]]): FieldInterface =
    val difference = preStatus.indices.map(x => preStatus(x).zip(postStatus(x)).count(x => x._1 != x._2)).sum
    if (difference.equals(1)) AddOnePoint.handle(field)
    else if (difference.equals(2)) AddTwoPoints.handle(field)
    else NextPlayer.handle(field)
