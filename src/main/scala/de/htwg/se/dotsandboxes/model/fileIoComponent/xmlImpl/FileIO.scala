package de.htwg.se.dotsandboxes.model
package fileIoComponent.xmlImpl

import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import fileIoComponent.FileIOInterface
import java.io._
import matrixComponent.matrixImpl.Status
import scala.xml.{Elem, NodeSeq, PrettyPrinter}
import scala.util.Try
import scala.util.{Success, Failure}
import de.htwg.se.dotsandboxes.util.PlayerType
import de.htwg.se.dotsandboxes.util.BoardSize
import de.htwg.se.dotsandboxes.util.PlayerSize

class FileIO extends FileIOInterface:
  override def save(field: FieldInterface): Either[String, String] =
    val filename = "field.xml"
    Try {
      val prettyPrinter = new PrettyPrinter(120, 4)
      val xml = prettyPrinter.format(fieldToXml(field))
      val printWriter = new PrintWriter(new File(filename))
      printWriter.write(xml)
      printWriter.close()
    } match
      case Success(_) => Right(s"Saved as $filename")
      case Failure(e) => Left(e.getMessage)
    
  def fieldToXml(field: FieldInterface): Elem =
    <field rowSize={field.maxPosY.toString} colSize={field.maxPosX.toString}>
      <playerList boardSize={field.boardSize.toString} playerSize={field.playerSize.toString} playerType={field.playerType.toString} currentPlayer={field.currentPlayerIndex.toString}>
        {field.playerList.indices.map(playerToXml(field, _))}
      </playerList>

      <status>
        {
          for
            row <- 0 until field.maxPosX
            col <- 0 until field.maxPosY
          yield statusCellToXml(field, row, col)
        }
      </status>

      <rows>
        {
          for
            row <- 0 until field.maxPosX
            col <- 0 until field.maxPosY
          yield rowCellToXml(field, row, col)
        }
      </rows>

      <cols>
        {
          for
            row <- 0 until field.maxPosX
            col <- 0 until field.maxPosY
          yield colCellToXml(field, row, col)
        }
      </cols>
    </field>

  def statusCellToXml(field: FieldInterface, row: Int, col: Int): Elem =
    <value row={row.toString} col={col.toString}>
      {field.getStatusCell(row, col)}
    </value>

  def rowCellToXml(field: FieldInterface, row: Int, col: Int): Elem =
    <value row={row.toString} col={col.toString}>
      {field.getRowCell(row, col)}
    </value>

  def colCellToXml(field: FieldInterface, row: Int, col: Int): Elem =
    <value row={row.toString} col={col.toString}>
      {field.getColCell(row, col)}
    </value>

  def playerToXml(field: FieldInterface, index: Int): Elem =
    <value index={index.toString}>
      {field.getPoints(index)}
    </value>

  override def load: FieldInterface =
    val file: Elem = scala.xml.XML.loadFile("field.xml")
    val boardSize: BoardSize   = Try(BoardSize.valueOf((file \\ "field" \ "playerList" \ "@boardSize").text)).getOrElse(BoardSize.Medium)
    val playerSize: PlayerSize = Try(PlayerSize.valueOf((file \\ "field" \ "playerList" \ "@playerSize").text)).getOrElse(PlayerSize.Two)
    val playerType: PlayerType = Try(PlayerType.valueOf((file \\ "field" \ "playerList" \ "@playerType").text)).getOrElse(PlayerType.Human)
    val initialField: FieldInterface = new Field(boardSize, Status.Empty, playerSize, playerType)

    val rowSize: Int = boardSize.dimensions._1
    val colSize: Int = boardSize.dimensions._2

    val statusSeq: NodeSeq = (file \\ "field" \ "status" \ "value")
    val fieldAfterStatus = statusSeq.foldLeft(initialField) { (field, rowNode) =>
      val row = (rowNode \ "@row").text.toInt
      val col = (rowNode \ "@col").text.toInt
      val value = rowNode.text.trim
      val status = value match
        case "B" => Status.Blue
        case "R" => Status.Red
        case "G" => Status.Green
        case "Y" => Status.Yellow
        case _   => Status.Empty
      field.putStatus(row, col, status)
    }

    val rowSeq: NodeSeq = (file \\ "field" \ "rows" \ "value")
    val fieldAfterRows = rowSeq.foldLeft(fieldAfterStatus) { (field, rowNode) =>
      val row = (rowNode \ "@row").text.toInt
      val col = (rowNode \ "@col").text.toInt
      val value = rowNode.text.trim.toBoolean
      field.putRow(row, col, value)
    }

    val colSeq: NodeSeq = (file \\ "field" \ "cols" \ "value")
    val fieldAfterCols = colSeq.foldLeft(fieldAfterRows) { (field, rowNode) =>
      val row = (rowNode \ "@row").text.toInt
      val col = (rowNode \ "@col").text.toInt
      val value = rowNode.text.trim.toBoolean
      field.putCol(row, col, value)
    }

    val scoreSeq: NodeSeq = (file \\ "field" \ "playerList" \ "value")
    val fieldAfterScores = scoreSeq.foldLeft(fieldAfterCols) { (field, player) =>
      val index = (player \ "@index").text.toInt
      val score = player.text.trim.toInt
      field.addPoints(index, score)
    }

    val curPlayerIndex: Int = (file \\ "field" \ "playerList" \ "@currentPlayer").text.toInt
    val finalField = fieldAfterScores.updatePlayer(curPlayerIndex)
    finalField
