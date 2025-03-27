package common.model.fieldService.fieldJson

import common.model.fieldService.FieldInterface
import play.api.libs.json.{JsObject, Json}

object FieldJsonConverter:
  def toJson(field: FieldInterface): JsObject =
    val (row, col) = field.boardSize.dimensions
    Json.obj(
      "field" -> Json.obj(
        "boardSize"      -> field.boardSize.toString,
        "playerSize"     -> field.playerSize.toString,
        "playerType"     -> field.playerType.toString,
        "currentPlayer"  -> field.currentPlayerIndex,
        "currentPoints"  -> field.currentPoints,
        "gameEnded"      -> field.isFinished,
        "winner"         -> field.winner,
        "colSize"        -> field.colSize(),
        "rowSize"        -> field.rowSize(),
        "status" -> (
          for
            x <- 0 until col
            y <- 0 until row
          yield Json.obj("x" -> x, "y" -> y, "value" -> field.getStatusCell(x, y).toString)
        ),
        "rows" -> (
          for
            x <- 0 until col+1
            y <- 0 until row
          yield Json.obj("x" -> x, "y" -> y, "value" -> field.getRowCell(x, y))
        ),
        "cols" -> (
          for
            x <- 0 until col
            y <- 0 until row+1
          yield Json.obj("x" -> x, "y" -> y, "value" -> field.getColCell(x, y))
        ),
        "playerList" -> field.playerList.zipWithIndex.map { case (player, index) =>
          Json.obj("index" -> index, "points" -> field.getPoints(index))
        }
      )
    )
