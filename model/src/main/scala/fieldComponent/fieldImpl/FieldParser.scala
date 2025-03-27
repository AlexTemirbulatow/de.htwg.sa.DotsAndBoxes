package fieldComponent.fieldImpl

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, Status}
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import scala.util.Try

object FieldParser:
  def fromJson(jsonField: String): FieldInterface =
    val json: JsValue = Json.parse(jsonField)
    val boardSize: BoardSize   = Try(BoardSize.valueOf((json \ "field" \ "boardSize").as[String])).getOrElse(BoardSize.Medium)
    val playerSize: PlayerSize = Try(PlayerSize.valueOf((json \ "field" \ "playerSize").as[String])).getOrElse(PlayerSize.Two)
    val playerType: PlayerType = Try(PlayerType.valueOf((json \ "field" \ "playerType").as[String])).getOrElse(PlayerType.Human)
    val initialField: FieldInterface = new Field(boardSize, Status.Empty, playerSize, playerType)

    val rowSize: Int = boardSize.dimensions._1
    val colSize: Int = boardSize.dimensions._2

    val statusResult: JsLookupResult = (json \ "field" \ "status")
    val fieldAfterStatus = (0 until rowSize * colSize).foldLeft(initialField) { (field, index) =>
      val x = (statusResult \\ "x")(index).as[Int]
      val y = (statusResult \\ "y")(index).as[Int]
      val value = (statusResult \\ "value")(index).as[String]
      val player = value match
        case "B" => Status.Blue
        case "R" => Status.Red
        case "G" => Status.Green
        case "Y" => Status.Yellow
        case _   => Status.Empty
      field.putStatus(x, y, player)
    }

    val rowResult: JsLookupResult = (json \ "field" \ "rows")
    val fieldAfterRows = (0 until rowSize * (colSize + 1)).foldLeft(fieldAfterStatus) { (field, index) =>
      val x = (rowResult \\ "x")(index).as[Int]
      val y = (rowResult \\ "y")(index).as[Int]
      val value = (rowResult \\ "value")(index).as[Boolean]
      field.putRow(x, y, value)
    }

    val colResult: JsLookupResult = (json \ "field" \ "cols")
    val fieldAfterCols = (0 until (rowSize + 1) * colSize).foldLeft(fieldAfterRows) { (field, index) =>
      val x = (colResult \\ "x")(index).as[Int]
      val y = (colResult \\ "y")(index).as[Int]
      val value = (colResult \\ "value")(index).as[Boolean]
      field.putCol(x, y, value)
    }

    val scoreResult: JsLookupResult = (json \ "field" \ "playerList")
    val fieldAfterScores = (0 until playerSize.size).foldLeft(fieldAfterCols) { (field, player) =>
      val index = (scoreResult \\ "index")(player).as[Int]
      val score = (scoreResult \\ "points")(player).as[Int]
      field.addPoints(index, score)
    }

    val curPlayerIndex = (json \ "field" \ "currentPlayer").as[Int]
    val finalField = fieldAfterScores.updatePlayer(curPlayerIndex)
    finalField
