package persistence
package fileIoComponent.jsonImpl

import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import fileIoComponent.FileIOInterface
import java.io._
import matrixComponent.matrixImpl.Status
import play.api.libs.json._
import scala.io.Source
import scala.util.Try
import scala.util.{Success, Failure}
import de.htwg.se.dotsandboxes.util.PlayerType
import de.htwg.se.dotsandboxes.util.BoardSize
import de.htwg.se.dotsandboxes.util.PlayerSize

class FileIO extends FileIOInterface:
  override def save(field: FieldInterface): Either[String, String] =
    val filename = "field.json"
    Try {
      val printWriter = new PrintWriter(new File(filename))
      printWriter.write(Json.prettyPrint(fieldToJson(field)))
      printWriter.close()
    } match
      case Success(_) => Right(s"Saved as $filename")
      case Failure(e) => Left(e.getMessage)

  def fieldToJson(field: FieldInterface): JsObject =
    Json.obj(
      "field" -> Json.obj(
        "boardSize" -> Json.toJson(field.boardSize.toString()),
        "playerSize" -> Json.toJson(field.playerSize.toString()),
        "playerType" -> Json.toJson(field.playerType.toString()),
        "currentPlayer" -> Json.toJson(field.currentPlayerIndex),
        "status" -> Json.toJson(
          for
            row <- 0 until field.maxPosX
            col <- 0 until field.maxPosY
          yield Json.obj("row" -> row, "col" -> col, "value" -> Json.toJson(field.getStatusCell(row, col).toString))
        ),
        "rows" -> Json.toJson(
          for
            row <- 0 until field.maxPosX
            col <- 0 until field.maxPosY
          yield Json.obj("row" -> row, "col" -> col, "value" -> Json.toJson(field.getRowCell(row, col).toString.toBoolean))
        ),
        "cols" -> Json.toJson(
          for
            row <- 0 until field.maxPosX
            col <- 0 until field.maxPosY
          yield Json.obj("row" -> row, "col" -> col, "value" -> Json.toJson(field.getColCell(row, col).toString.toBoolean))
        ),
        "playerList" -> Json.toJson(
          for playerIndex <- 0 until field.playerList.size
          yield Json.obj("index" -> playerIndex, "points" -> Json.toJson(field.getPoints(playerIndex)))
        )
      )
    )

  override def load: FieldInterface =
    val source: String = Source.fromFile("field.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val boardSize: BoardSize   = Try(BoardSize.valueOf((json \ "field" \ "boardSize").as[String])).getOrElse(BoardSize.Medium)
    val playerSize: PlayerSize = Try(PlayerSize.valueOf((json \ "field" \ "playerSize").as[String])).getOrElse(PlayerSize.Two)
    val playerType: PlayerType = Try(PlayerType.valueOf((json \ "field" \ "playerType").as[String])).getOrElse(PlayerType.Human)
    val initialField: FieldInterface = new Field(boardSize, Status.Empty, playerSize, playerType)

    val rowSize: Int = boardSize.dimensions._1
    val colSize: Int = boardSize.dimensions._2

    val statusResult: JsLookupResult = (json \ "field" \ "status")
    val fieldAfterStatus = (0 until rowSize * colSize).foldLeft(initialField) { (field, index) =>
      val row = (statusResult \\ "row")(index).as[Int]
      val col = (statusResult \\ "col")(index).as[Int]
      val value = (statusResult \\ "value")(index).as[String]
      val player = value match
        case "B" => Status.Blue
        case "R" => Status.Red
        case "G" => Status.Green
        case "Y" => Status.Yellow
        case _   => Status.Empty
      field.putStatus(row, col, player)
    }

    val rowResult: JsLookupResult = (json \ "field" \ "rows")
    val fieldAfterRows = (0 until rowSize * colSize).foldLeft(fieldAfterStatus) { (field, index) =>
      val row = (rowResult \\ "row")(index).as[Int]
      val col = (rowResult \\ "col")(index).as[Int]
      val value = (rowResult \\ "value")(index).as[Boolean]
      field.putRow(row, col, value)
    }

    val colResult: JsLookupResult = (json \ "field" \ "cols")
    val fieldAfterCols = (0 until rowSize * colSize).foldLeft(fieldAfterRows) { (field, index) =>
      val row = (colResult \\ "row")(index).as[Int]
      val col = (colResult \\ "col")(index).as[Int]
      val value = (colResult \\ "value")(index).as[Boolean]
      field.putCol(row, col, value)
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
