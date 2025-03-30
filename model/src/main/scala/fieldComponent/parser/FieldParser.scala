package fieldComponent.parser

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, Status}
import fieldComponent.fieldImpl.Field
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import scala.util.Try
import scala.xml.{Elem, NodeSeq, XML}

object FieldParser:
  def fromJson(jsonField: String): FieldInterface =
    val json: JsValue = Json.parse(jsonField)
    val boardSize: BoardSize   = Try(BoardSize.valueOf((json \ "field" \ "boardSize").as[String])).getOrElse(throw new RuntimeException("Invalid Board Size."))
    val playerSize: PlayerSize = Try(PlayerSize.valueOf((json \ "field" \ "playerSize").as[String])).getOrElse(throw new RuntimeException("Invalid Player Size."))
    val playerType: PlayerType = Try(PlayerType.valueOf((json \ "field" \ "playerType").as[String])).getOrElse(throw new RuntimeException("Invalid Player Type."))
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

    val currPlayerIndex = (json \ "field" \ "currPlayerIndex").as[Int]
    val finalField = fieldAfterScores.updatePlayer(currPlayerIndex)
    finalField

  def fromXml(xmlField: String): FieldInterface =
    val elem: Elem = XML.loadString(xmlField)
    val boardSize: BoardSize   = Try(BoardSize.valueOf((elem \\ "field" \ "playerList" \ "@boardSize").text)).getOrElse(throw new RuntimeException("Invalid Board Size."))
    val playerSize: PlayerSize = Try(PlayerSize.valueOf((elem \\ "field" \ "playerList" \ "@playerSize").text)).getOrElse(throw new RuntimeException("Invalid Player Size."))
    val playerType: PlayerType = Try(PlayerType.valueOf((elem \\ "field" \ "playerList" \ "@playerType").text)).getOrElse(throw new RuntimeException("Invalid Player Type."))
    val initialField: FieldInterface = new Field(boardSize, Status.Empty, playerSize, playerType)

    val rowSize: Int = boardSize.dimensions._1
    val colSize: Int = boardSize.dimensions._2

    val statusSeq: NodeSeq = (elem \\ "field" \ "status" \ "value")
    val fieldAfterStatus = statusSeq.foldLeft(initialField) { (field, rowNode) =>
      val x = (rowNode \ "@x").text.toInt
      val y = (rowNode \ "@y").text.toInt
      val value = rowNode.text.trim
      val status = value match
        case "B" => Status.Blue
        case "R" => Status.Red
        case "G" => Status.Green
        case "Y" => Status.Yellow
        case _   => Status.Empty
      field.putStatus(x, y, status)
    }

    val rowSeq: NodeSeq = (elem \\ "field" \ "rows" \ "value")
    val fieldAfterRows = rowSeq.foldLeft(fieldAfterStatus) { (field, rowNode) =>
      val x = (rowNode \ "@x").text.toInt
      val y = (rowNode \ "@y").text.toInt
      val value = rowNode.text.trim.toBoolean
      field.putRow(x, y, value)
    }

    val colSeq: NodeSeq = (elem \\ "field" \ "cols" \ "value")
    val fieldAfterCols = colSeq.foldLeft(fieldAfterRows) { (field, rowNode) =>
      val x = (rowNode \ "@x").text.toInt
      val y = (rowNode \ "@y").text.toInt
      val value = rowNode.text.trim.toBoolean
      field.putCol(x, y, value)
    }

    val scoreSeq: NodeSeq = (elem \\ "field" \ "playerList" \ "value")
    val fieldAfterScores = scoreSeq.foldLeft(fieldAfterCols) { (field, player) =>
      val index = (player \ "@index").text.toInt
      val score = player.text.trim.toInt
      field.addPoints(index, score)
    }

    val currPlayerIndex: Int = (elem \\ "field" \ "playerList" \ "@currPlayerIndex").text.toInt
    val finalField = fieldAfterScores.updatePlayer(currPlayerIndex)
    finalField
