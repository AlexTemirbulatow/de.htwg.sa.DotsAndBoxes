package fileIoComponent.xmlImpl

import java.io.{PrintWriter, File}
import scala.xml.{Elem, NodeSeq, PrettyPrinter}
import scala.util.{Success, Failure, Try}

import fileIoComponent.FileIOInterface
import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import de.github.dotsandboxes.lib.{PlayerType, BoardSize, PlayerSize, Status}

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
            x <- 0 until field.maxPosX
            y <- 0 until field.maxPosY
          yield statusCellToXml(field, x, y)
        }
      </status>

      <rows>
        {
          for
            x <- 0 until field.maxPosY
            y <- 0 until field.maxPosY
          yield rowCellToXml(field, x, y)
        }
      </rows>

      <cols>
        {
          for
            x <- 0 until field.maxPosX
            y <- 0 to field.maxPosY
          yield colCellToXml(field, x, y)
        }
      </cols>
    </field>

  def statusCellToXml(field: FieldInterface, x: Int, y: Int): Elem =
    <value x={x.toString} y={y.toString}>
      {field.getStatusCell(x, y)}
    </value>

  def rowCellToXml(field: FieldInterface, x: Int, y: Int): Elem =
    <value x={x.toString} y={y.toString}>
      {field.getRowCell(x, y)}
    </value>

  def colCellToXml(field: FieldInterface, x: Int, y: Int): Elem =
    <value x={x.toString} y={y.toString}>
      {field.getColCell(x, y)}
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

    val rowSeq: NodeSeq = (file \\ "field" \ "rows" \ "value")
    val fieldAfterRows = rowSeq.foldLeft(fieldAfterStatus) { (field, rowNode) =>
      val x = (rowNode \ "@x").text.toInt
      val y = (rowNode \ "@y").text.toInt
      val value = rowNode.text.trim.toBoolean
      field.putRow(x, y, value)
    }

    val colSeq: NodeSeq = (file \\ "field" \ "cols" \ "value")
    val fieldAfterCols = colSeq.foldLeft(fieldAfterRows) { (field, rowNode) =>
      val x = (rowNode \ "@x").text.toInt
      val y = (rowNode \ "@y").text.toInt
      val value = rowNode.text.trim.toBoolean
      field.putCol(x, y, value)
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
