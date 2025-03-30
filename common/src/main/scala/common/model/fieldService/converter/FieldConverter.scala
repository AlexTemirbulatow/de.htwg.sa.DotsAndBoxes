package common.model.fieldService.converter

import common.model.fieldService.FieldInterface
import play.api.libs.json.{JsObject, Json}
import scala.xml.Elem

object FieldConverter:
  def toJson(field: FieldInterface): JsObject =
    val (row, col) = field.boardSize.dimensions
    Json.obj(
      "field" -> Json.obj(
        "boardSize"      -> field.boardSize.toString,
        "playerSize"     -> field.playerSize.toString,
        "playerType"     -> field.playerType.toString,
        "currentPlayer"  -> field.currentPlayerIndex,
        "currentPoints"  -> field.currentPlayer.points,
        "gameEnded"      -> field.isFinished,
        "winner"         -> field.winner,
        "colSize"        -> field.colSize,
        "rowSize"        -> field.rowSize,
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
          Json.obj("index" -> index, "points" -> field.getPlayerPoints(index))
        }
      )
    )

  def toXml(field: FieldInterface): Elem =
    val (row, col) = field.boardSize.dimensions
    <field rowSize={field.maxPosY.toString} colSize={field.maxPosX.toString}>
      <playerList boardSize={field.boardSize.toString} playerSize={field.playerSize.toString} playerType={field.playerType.toString} currentPlayer={field.currentPlayerIndex.toString}>
        {field.playerList.indices.map(playerToXml(field, _))}
      </playerList>

      <status>
        {
          for
            x <- 0 until col
            y <- 0 until row
          yield statusCellToXml(field, x, y)
        }
      </status>

      <rows>
        {
          for
            x <- 0 until col+1
            y <- 0 until row
          yield rowCellToXml(field, x, y)
        }
      </rows>

      <cols>
        {
          for
            x <- 0 until col
            y <- 0 until row+1
          yield colCellToXml(field, x, y)
        }
      </cols>
    </field>

  private def statusCellToXml(field: FieldInterface, x: Int, y: Int): Elem =
    <value x={x.toString} y={y.toString}>
      {field.getStatusCell(x, y)}
    </value>

  private def rowCellToXml(field: FieldInterface, x: Int, y: Int): Elem =
    <value x={x.toString} y={y.toString}>
      {field.getRowCell(x, y)}
    </value>

  private def colCellToXml(field: FieldInterface, x: Int, y: Int): Elem =
    <value x={x.toString} y={y.toString}>
      {field.getColCell(x, y)}
    </value>

  private def playerToXml(field: FieldInterface, index: Int): Elem =
    <value index={index.toString}>
      {field.getPlayerPoints(index)}
    </value>
