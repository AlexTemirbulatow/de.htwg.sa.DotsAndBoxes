package de.htwg.se.dotsandboxes
package util

import model.matrixComponent.matrixImpl.Status
import model.fieldComponent.FieldInterface
import playerState.{AddOnePoint, AddTwoPoints, NextPlayer}

object PlayerStrategy:
  def updatePlayer(field: FieldInterface, preStatus: Vector[Vector[Status]], postStatus: Vector[Vector[Status]]): FieldInterface =
    val difference = preStatus
      .zip(postStatus)
      .flatMap { case (preRow, postRow) =>
        preRow.zip(postRow)
      }
      .count { case (pre, post) => pre != post }

    if (difference.equals(1)) AddOnePoint.handle(field)
    else if (difference.equals(2)) AddTwoPoints.handle(field)
    else NextPlayer.handle(field)
