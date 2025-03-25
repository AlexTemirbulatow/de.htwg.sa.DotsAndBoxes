package fieldComponent.fieldImpl

import fieldComponent.FieldInterface
import matrixComponent.MatrixInterface
import matrixComponent.matrixImpl.Matrix
import de.github.dotsandboxes.lib.{PlayerType, BoardSize, PlayerSize, SquareCase, Player, Status, Move, CellData}
import play.api.libs.json.{Json, JsObject, JsValue, JsLookupResult}
import scala.util.Try

case class Field(matrix: MatrixInterface) extends FieldInterface:
  def this(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType) =
    this(new Matrix(boardSize, status, playerSize, playerType))
  
  override def bar(
    length: Int,
    cellNum: Int,
    rowIndex: Int,
    rowFunc: (Int, Int, Int) => String
  ): String =
    List
      .tabulate(cellNum)(colIndex => rowFunc(rowIndex, colIndex, length))
      .mkString(Connectors("O"), Connectors("O"), Connectors("O")) + "\n"

  override def cells(
    rowSize: Int,
    length: Int,
    height: Int,
    colFunc: (Int, Int, Int) => String
  ): String =
    List.fill(height)(
      List
        .tabulate(maxPosY + 1)(colIndex => colFunc(rowSize, colIndex, length))
        .mkString + "\n"
    ).mkString

  override def mesh(length: Int, height: Int): String =
    List
      .tabulate(maxPosX)(x => bar(length, maxPosY, x, rows) + cells(x, length, height, columns))
      .mkString + bar(length, maxPosY, maxPosX, rows)
      
  override def rows(rowIndex: Int, colIndex: Int, length: Int): String = getRowCell(rowIndex, colIndex) match
    case false => Connectors("-") * length
    case true  => Connectors("=") * length
  override def columns(rowIndex: Int, colIndex: Int, length: Int): String = getColCell(rowIndex, colIndex) match
    case false => Connectors("¦") + status(rowIndex, colIndex, length)
    case true  => Connectors("‖") + status(rowIndex, colIndex, length)
  override def boardSize: BoardSize = matrix.getBoardSize
  override def playerSize: PlayerSize = matrix.getPlayerSize
  override def status(rowIndex: Int, colIndex: Int, length: Int): String = (colIndex < maxPosY) match
    case false => Connectors("")
    case true  => space(length) + getStatusCell(rowIndex, colIndex) + space(length)
  override def winner: String = if (playerList.indices.map(playerList(_).points).count(_ == playerList.maxBy(_._2).points) > 1) "It's a draw!"
  else s"Player ${playerList.maxBy(_._2).playerId} wins!"
  override def stats: String = playerList.indices.map(x => s"Player ${playerList(x).playerId} [points: ${playerList(x).points}]").mkString("\n")
  override def getStatusCell(row: Int, col: Int): Status = matrix.statusCell(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = matrix.rowCell(row, col)
  override def getColCell(row: Int, col: Int): Boolean = matrix.colCell(row, col)
  override def checkAllCells(squareCase: SquareCase, x: Int, y: Int): Vector[Boolean] = matrix.checkAllCells(squareCase, x, y)
  override def cellsToCheck(squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)] = matrix.cellsToCheck(squareCase, x, y)
  override def putStatus(row: Int, col: Int, status: Status): Field = copy(matrix.replaceStatusCell(row, col, status))
  override def putRow(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceRowCell(row, col, value))
  override def putCol(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceColCell(row, col, value))
  override def getUnoccupiedRowCoord(): Vector[(Int, Int, Int)] = matrix.getUnoccupiedRowCoord()
  override def getUnoccupiedColCoord(): Vector[(Int, Int, Int)] = matrix.getUnoccupiedColCoord()
  override def isFinished: Boolean = (matrix.vectorRow ++ matrix.vectorCol).forall(_.forall(_.equals(true)))
  override def isEdge(move: Move): Boolean = matrix.isEdge(move)
  override def checkSquare(squareCase: SquareCase, x: Int, y: Int): Field = copy(matrix.checkSquare(squareCase, x, y))
  override def currentPlayer: Player = matrix.getCurrentPlayer
  override def currentPlayerId: String = matrix.currentPlayerInfo._1
  override def currentPlayerIndex: Int = matrix.currentPlayerInfo._2
  override def currentStatus: Vector[Vector[Status]] = matrix.vectorStatus
  override def currentPoints: Int = matrix.currentPoints
  override def nextPlayer: Field = copy(matrix.changePlayer)
  override def updatePlayer(curPlayerIndex: Int): Field = copy(matrix.updatePlayer(curPlayerIndex))
  override def playerIndex: Int = matrix.playerIndex
  override def addPoints(curPlayerIndex: Int, points: Int): Field = copy(matrix.addPoints(curPlayerIndex, points))
  override def playerList: Vector[Player] = matrix.playerList
  override def playerType: PlayerType = matrix.playerList.last.playerType
  override def getPoints(index: Int): Int = matrix.getPoints(index)
  override def rowSize(): Int = matrix.rowSize()
  override def colSize(): Int = matrix.colSize()
  override def space(length: Int): String = " " * ((length - 1) / 2)
  override def toCellData: CellData =
    CellData(
      Vector.tabulate(maxPosY, maxPosY)((row, col) => getRowCell(row, col)),
      Vector.tabulate(maxPosX, maxPosY+1)((row, col) => getColCell(row, col)),
      Vector.tabulate(maxPosX, maxPosY)((row, col) => getStatusCell(row, col).toString)
    )
  override def toJson: JsObject =
    Json.obj(
      "field" -> Json.obj(
        "boardSize" -> Json.toJson(boardSize.toString()),
        "playerSize" -> Json.toJson(playerSize.toString()),
        "playerType" -> Json.toJson(playerType.toString()),
        "currentPlayer" -> Json.toJson(currentPlayerIndex),
        "status" -> Json.toJson(
          for
            x <- 0 until maxPosX
            y <- 0 until maxPosY
          yield Json.obj("x" -> x, "y" -> y, "value" -> Json.toJson(getStatusCell(x, y).toString))
        ),
        "rows" -> Json.toJson(
          for
            x <- 0 until maxPosY
            y <- 0 until maxPosY
          yield Json.obj("x" -> x, "y" -> y, "value" -> Json.toJson(getRowCell(x, y).toString.toBoolean))
        ),
        "cols" -> Json.toJson(
          for
            x <- 0 until maxPosX
            y <- 0 to maxPosY
          yield Json.obj("x" -> x, "y" -> y, "value" -> Json.toJson(getColCell(x, y).toString.toBoolean))
        ),
        "playerList" -> Json.toJson(
          for playerIndex <- 0 until playerList.size
          yield Json.obj("index" -> playerIndex, "points" -> Json.toJson(getPoints(playerIndex)))
        )
      )
    )
  override def fromJson(jsonField: String): FieldInterface =
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
  override def toString = mesh(7, 2)
  override val maxPosX = matrix.maxPosX
  override val maxPosY = matrix.maxPosY
  override val vectorRow = matrix.vectorRow
  override val vectorCol = matrix.vectorCol
