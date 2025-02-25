package model
package fieldComponent.fieldImpl

import de.htwg.se.dotsandboxes.util.Move
import de.htwg.se.dotsandboxes.util.moveState.SquareState
import matrixComponent.matrixImpl.{Matrix, Player, Status}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.dotsandboxes.util.{BoardSize, PlayerSize, PlayerType}
import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface

class FieldSpec extends AnyWordSpec {
  "A Dots and Boxes Field" when {
    "initialized empty" should {
      val field1 = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val field2 = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val field3 = new Field(BoardSize.Large, Status.Empty, PlayerSize.Two, PlayerType.Human)

      "have a proper bar as String" in {
        field1.bar(7, 1, 0, field1.rows) should be(
          "O-------O\n"
        )
        field1.bar(7, 2, 0, field1.rows) should be(
          "O-------O-------O\n"
        )
        field1.bar(7, 4, 0, field1.rows) should be(
          "O-------O-------O-------O-------O\n"
        )
        field2.bar(7, 5, 2, field2.rows) should be(
          "O-------O-------O-------O-------O-------O\n"
        )
        field2.bar(2, 5, 0, field2.rows) should be(
          "O--O--O--O--O--O\n"
        )
        field3.bar(7, 6, 2, field3.rows) should be(
          "O-------O-------O-------O-------O-------O-------O\n"
        )
      }
      "have a scalable bar" in {
        field1.bar(1, 1, 0, field1.rows) should be("O-O\n")
        field2.bar(2, 1, 0, field2.rows) should be("O--O\n")
        field3.bar(2, 2, 0, field3.rows) should be("O--O--O\n")
      }
      "have cells as proper String" in {
        field1.cells(0, 7, 2, field1.columns) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
        field2.cells(0, 7, 2, field2.columns) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
        field3.cells(0, 7, 2, field3.columns) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
        field1.cells(1, 3, 2, field1.columns) should be(
          "¦ - ¦ - ¦ - ¦ - ¦\n" +
          "¦ - ¦ - ¦ - ¦ - ¦\n"
        )
        field1.cells(2, 7, 3, field1.columns) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
      }
      "have scalable cells" in {
        field1.cells(0, 1, 1, field1.columns) should be("¦-¦-¦-¦-¦\n")
        field1.cells(0, 5, 1, field1.columns) should be("¦  -  ¦  -  ¦  -  ¦  -  ¦\n")
        field2.cells(0, 3, 1, field2.columns) should be("¦ - ¦ - ¦ - ¦ - ¦ - ¦\n")
        field3.cells(0, 7, 2, field3.columns) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
      }
      "have a mesh in form" in {
        field2.mesh(3, 2) should be(
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n"
        )
        field2.mesh(3, 1) should be(
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n" +
          "¦ - ¦ - ¦ - ¦ - ¦ - ¦\n" +
          "O---O---O---O---O---O\n"
        )
        field1.mesh(7, 2) should be(
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n"
          
        )
        field1.mesh(1, 2) should be(
          "O-O-O-O-O\n" +
          "¦-¦-¦-¦-¦\n" +
          "¦-¦-¦-¦-¦\n" +
          "O-O-O-O-O\n" +
          "¦-¦-¦-¦-¦\n" +
          "¦-¦-¦-¦-¦\n" +
          "O-O-O-O-O\n" +
          "¦-¦-¦-¦-¦\n" +
          "¦-¦-¦-¦-¦\n" +
          "O-O-O-O-O\n"
        )
        field2.mesh(7, 1) should be(
          "O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O\n"
        )
        field3.mesh(7, 2) should be(
          "O-------O-------O-------O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O-------O-------O-------O-------O\n"
        )
      }
    }
    "initialized blue" should {
      val field4 = new Field(BoardSize.Medium, Status.Blue, PlayerSize.Two, PlayerType.Human)
      "have a mesh with Blue fillings" in {
        field4.mesh(7, 2) should be(
          "O-------O-------O-------O-------O-------O\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "O-------O-------O-------O-------O-------O\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "O-------O-------O-------O-------O-------O\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "O-------O-------O-------O-------O-------O\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "¦   B   ¦   B   ¦   B   ¦   B   ¦   B   ¦\n" +
          "O-------O-------O-------O-------O-------O\n"
        )
      }
    }
    "initialized empty" should {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "be empty initially" in {
        field.toString should be(
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n"
        )
      }
      "have Blue and Red after putCell()" in {
        field.putStatus(0, 0, Status.Blue).putStatus(1, 1, Status.Red).toString should be(
          "O-------O-------O-------O-------O\n" +
          "¦   B   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   B   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   R   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   R   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n"
        )
      }
      "have changeable lines in form of '=' and '‖' after putCell()" in {
        field.putRow(0, 0, true).putCol(0, 0, true).toString should be(
          "O=======O-------O-------O-------O\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n"
        )
      }
      "access Matrix to check line and filling status" in {
        val bar = field.putRow(0, 0, true)
        bar.rows(1, 0, 7) should be("-------")
        bar.rows(0, 0, 7) should be("=======")

        val cells = field.putCol(0, 0, true)
        cells.columns(1, 0, 7) should be("¦   -   ")
        cells.columns(0, 0, 7) should be("‖   -   ")
        cells.columns(1, 2, 7) should be("¦   -   ")

        val square = field.putStatus(0, 0, Status.Blue).putStatus(1, 1, Status.Red)
        square.status(1, 0, 7) should be("   -   ")
        square.status(0, 0, 7) should be("   B   ")
        square.status(1, 1, 7) should be("   R   ")
      }
      "return correct size" in {
        field.rowSize() should be(4)
        field.colSize() should be(5)
      }
      "return correct space" in {
        field.space(3) should be(" ")
        field.space(5) should be("  ")
        field.space(7) should be("   ")
      }
      "give access to its cells" in {
        field.getStatusCell(0, 0) should be(Status.Empty)
        field.getRowCell(0, 0) shouldBe false
        field.getColCell(0, 0) shouldBe false

        field.putRow(0, 0, true).getRowCell(0, 0) shouldBe true
        field.putCol(0, 0, true).getColCell(0, 0) shouldBe true
      }
      "return cells to check" in {
        val cellsToCheckDown: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareState.DownCase, 0, 0)
        cellsToCheckDown should be(Vector((1, 1, 0), (2, 0, 0), (2, 0, 1)))

        val cellsToCheckUp: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareState.UpCase, 3, 0)
        cellsToCheckUp should be(Vector((1, 2, 0), (2, 2, 0), (2, 2, 1)))
        
        val cellsToCheckRight: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareState.RightCase, 0, 0)
        cellsToCheckRight should be(Vector((2, 0, 1), (1, 0, 0), (1, 1, 0)))

        val cellsToCheckLeft: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareState.LeftCase, 0, 4)
        cellsToCheckLeft should be(Vector((2, 0, 3), (1, 0, 3), (1, 1, 3)))
      }
      "return boolean for neighbor cells" in {
        val checkCellsDown: Vector[Boolean] = field.checkAllCells(SquareState.DownCase, 0, 0)
        checkCellsDown should be(Vector(false, false, false))

        val newField: FieldInterface = field.putRow(1, 0, true).putCol(0, 0, true)

        val newCheckCellsDown: Vector[Boolean] = newField.checkAllCells(SquareState.DownCase, 0, 0)
        newCheckCellsDown should be(Vector(true, true, false))
      }
      "check if a move is a edge case" in {
        val move1 = new Move(1, 0, 0, true)
        val move2 = new Move(1, 1, 1, true)
        val move3 = new Move(2, 0, 0, true)
        val move4 = new Move(2, 1, 1, true)

        field.isEdge(move1) shouldBe true
        field.isEdge(move2) shouldBe false

        field.isEdge(move3) shouldBe true
        field.isEdge(move4) shouldBe false
      }
      "do a move in edge case" in {
        field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(1, 0, true).putCol(2, 0, true)
          .putCol(0, 4, true).putCol(1, 4, true).putCol(2, 4, true)
          .toString should be(
          "O=======O=======O=======O=======O\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ‖\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ‖\n" +
          "O-------O-------O-------O-------O\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ‖\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ‖\n" +
          "O-------O-------O-------O-------O\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ‖\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ‖\n" +
          "O=======O=======O=======O=======O\n"
        )

        field.putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true).putCol(0, 1, true).checkSquare(SquareState.RightCase, 0, 0).toString should be(
          "O=======O-------O-------O-------O\n" +
          "‖   B   ‖   -   ¦   -   ¦   -   ¦\n" +
          "‖   B   ‖   -   ¦   -   ¦   -   ¦\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n"
        )
      }
      "do a move in mud case" in {
        val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

        field.checkSquare(SquareState.DownCase, 1, 1) should be(field)
        field.putRow(1, 1, true).putRow(2, 1, true).putCol(1, 1, true).putCol(1, 2, true).checkSquare(SquareState.DownCase, 1, 1).toString should be(
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O=======O-------O-------O\n" +
          "¦   -   ‖   B   ‖   -   ¦   -   ¦\n" +
          "¦   -   ‖   B   ‖   -   ¦   -   ¦\n" +
          "O-------O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n"
        )
      }
      "return information about current game state" in {
        var field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

        val matrixVector1 =
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )

        val matrixVector2 =
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Red", 0, Status.Red, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )

        val matrixVector3 =
          Matrix(
            Vector(Vector(Status.Blue)),
            Vector(Vector(true), Vector(true)),
            Vector(Vector(true, true)),
            Vector(Player("Blue", 1, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 1, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )

        val matrixVector4 =
          Matrix(
            Vector(Vector(Status.Blue)),
            Vector(Vector(true), Vector(true)),
            Vector(Vector(true, true)),
            Vector(Player("Blue", 1, Status.Blue, PlayerType.Human), Player("Red", 5, Status.Red, PlayerType.Human)),
            Player("Red", 5, Status.Red, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )

        field.currentPlayerId should be("Blue")
        field.currentPlayerIndex should be(0)
        field.playerIndex should be(0)
        field.currentStatus should be(
          Vector(
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
          )
        )
        field.currentPoints should be(0)

        field.isFinished shouldBe false

        field = field.putStatus(0, 0, Status.Blue).putRow(0, 0, true).putCol(0, 0, true).putRow(1, 0, true).putCol(0, 1, true)
        field.currentPlayerId should be("Blue")
        field.currentStatus should be(
          Vector(
            Vector(Status.Blue, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
          )
        )

        for {
          x <- 0 until field.maxPosY
          y <- 0 until field.maxPosY
        } {
          field = field.putRow(x, y, true)
        }

        for {
          x <- 0 until field.maxPosX
          y <- 0 to field.maxPosY
        } {
          field = field.putCol(x, y, true)
        }

        field = field.addPoints(field.playerIndex, 1).updatePlayer(field.playerIndex)
        field.currentPoints should be(1)

        field = field.addPoints(1, 5).updatePlayer(1)
        field.currentPoints should be(5)

        field.getPoints(0) should be(1)
        field.getPoints(1) should be(5)

        field.isFinished shouldBe true

        field.winner should be("Player Red wins!")

        field.stats should be(
          "Player Blue [points: 1]\n" +
          "Player Red [points: 5]"
        )

        val field2 = new Field(
          Matrix(
            Vector(Vector(Status.Blue, Status.Blue), Vector(Status.Red, Status.Red)),
            Vector(Vector(true, true), Vector(true, true), Vector(true, true)),
            Vector(Vector(true, true, true), Vector(true, true, true)),
            Vector(Player("Blue", 2, Status.Blue, PlayerType.Human), Player("Red", 2, Status.Red, PlayerType.Human)),
            Player("Blue", 2, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )
        )

        field2.winner should be("It's a draw!")

        field2.stats should be(
          "Player Blue [points: 2]\n" +
          "Player Red [points: 2]"
        )

        val field3 = new Field(
          Matrix(
            Vector(Vector(Status.Blue, Status.Blue), Vector(Status.Red, Status.Red)),
            Vector(Vector(true, true), Vector(true, true), Vector(true, true)),
            Vector(Vector(true, true, true), Vector(true, true, true)),
            Vector(Player("Blue", 2, Status.Blue, PlayerType.Human), Player("Red", 2, Status.Red, PlayerType.Human), Player("Green", 1, Status.Green, PlayerType.Human)),
            Player("Blue", 2, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )
        )

        field3.winner should be("It's a draw!")

        field3.stats should be(
          "Player Blue [points: 2]\n" +
          "Player Red [points: 2]\n" +
          "Player Green [points: 1]"
        )
      }
      "return the correct player list" in {
        val field0 = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val field1 = new Field(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Human)
        val field2 = new Field(BoardSize.Small, Status.Empty, PlayerSize.Four, PlayerType.Human)

        field0.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)))
        field1.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human)))
        field2.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human), Player("Yellow", 0, Status.Yellow, PlayerType.Human)))
      }
    }
  }
}
