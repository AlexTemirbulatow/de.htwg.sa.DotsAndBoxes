package core.controllerComponent.utils.playerStrategy

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.Status

object PlayerStrategy:
  def updatePlayer(field: FieldInterface, preStatus: Vector[Vector[Status]], postStatus: Vector[Vector[Status]]): String =
    val difference = preStatus
      .zip(postStatus)
      .flatMap { case (preRow, postRow) =>
        preRow.zip(postRow)
      }
      .count { case (pre, post) => pre != post }

    if (difference.equals(1)) AddOnePoint.handle(field)
    else if (difference.equals(2)) AddTwoPoints.handle(field)
    else NextPlayer.handle(field)
