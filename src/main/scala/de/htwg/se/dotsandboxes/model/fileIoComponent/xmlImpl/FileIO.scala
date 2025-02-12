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

class FileIO extends FileIOInterface:
  override def save(field: FieldInterface): Either[String, String] =
    val filename = "field.xml"
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(fieldToXml(field))
    Try {
      val printWriter = new PrintWriter(new File(filename))
      printWriter.write(xml)
      printWriter.close()
    } match
      case Success(_) => Right(s"Saved as $filename")
      case Failure(e) => Left(e.getMessage)
    
  def fieldToXml(field: FieldInterface): Elem =
    <field rowSize={field.maxPosY.toString} colSize={field.maxPosX.toString}>
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

      <playerList playerSize={field.playerList.size.toString} currentPlayer={field.currentPlayerIndex.toString}>
        {field.playerList.indices.map(playerToXml(field, _))}
      </playerList>
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
    val rowSize: Int = (file \\ "field" \ "@rowSize").text.toInt
    val colSize: Int = (file \\ "field" \ "@colSize").text.toInt
    val playerSize: Int = (file \\ "field" \ "playerList" \ "@playerSize").text.toInt
    val initialField: FieldInterface = new Field(rowSize, colSize, Status.Empty, playerSize)

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
