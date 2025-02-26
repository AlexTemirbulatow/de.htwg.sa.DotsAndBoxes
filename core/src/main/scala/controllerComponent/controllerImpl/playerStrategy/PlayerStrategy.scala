package controllerComponent.controllerImpl.playerStrategy

import fieldComponent.FieldInterface
import lib.Status

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
