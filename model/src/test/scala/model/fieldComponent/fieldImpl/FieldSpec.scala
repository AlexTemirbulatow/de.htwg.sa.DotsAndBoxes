package model.fieldComponent.fieldImpl

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field
import model.matrixComponent.matrixImpl.Matrix
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class FieldSpec extends AnyWordSpec {
  "A Dots and Boxes Field" when {
    "initialized empty" should {
      val field1: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val field2: Field = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val field3: Field = new Field(BoardSize.Large, Status.Empty, PlayerSize.Two, PlayerType.Human)

      "have a proper space String representation" in {
        field1.space(1) should be("")
        field1.space(5) should be("  ")
        field1.space(10) should be("    ")
      }
      "have a proper status String representation" in {
        field1.status(0, 0, 7) should be("   -   ")
        field1.putStatus(1, 1, Status.Blue).asInstanceOf[Field].status(1, 1, 5) should be("  B  ")
        field1.putStatus(1, 2, Status.Red).asInstanceOf[Field].status(1, 2, 7) should be("   R   ")
        field1.putStatus(1, 3, Status.Green).asInstanceOf[Field].status(1, 3, 3) should be(" G ")
        field1.putStatus(2, 2, Status.Yellow).asInstanceOf[Field].status(2, 2, 5) should be("  Y  ")
        field1.status(1, 9, 7) should be("")
      }
      "have a proper rows String representation" in {
        field1.rows(0, 0, 1) should be("-")
        field1.rows(0, 0, 5) should be("-----")
        field1.putRow(1, 1, true).asInstanceOf[Field].rows(1, 1, 5) should be("=====")
      }
      "have a proper columns String representation" in {
        field1.columns(0, 0, 5) should be("¦  -  ")
        field1.columns(0, 0, 7) should be("¦   -   ")
        field1.putCol(1, 1, true).asInstanceOf[Field].columns(1, 1, 7) should be("‖   -   ")
      }
      "have a proper bar as String" in {
        field1.bar(7, 1, 0) should be(
          "O-------O\n"
        )
        field1.bar(7, 2, 0) should be(
          "O-------O-------O\n"
        )
        field1.bar(7, 4, 0) should be(
          "O-------O-------O-------O-------O\n"
        )
        field2.bar(7, 5, 2) should be(
          "O-------O-------O-------O-------O-------O\n"
        )
        field2.bar(2, 5, 0) should be(
          "O--O--O--O--O--O\n"
        )
        field3.bar(7, 6, 2) should be(
          "O-------O-------O-------O-------O-------O-------O\n"
        )
      }
      "have a scalable bar" in {
        field1.bar(1, 1, 0) should be("O-O\n")
        field2.bar(2, 1, 0) should be("O--O\n")
        field3.bar(2, 2, 0) should be("O--O--O\n")
      }
      "have cells as proper String" in {
        field1.cells(0, 7, 2) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
        field2.cells(0, 7, 2) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
        field3.cells(0, 7, 2) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
        field1.cells(1, 3, 2) should be(
          "¦ - ¦ - ¦ - ¦ - ¦\n" +
          "¦ - ¦ - ¦ - ¦ - ¦\n"
        )
        field1.cells(2, 7, 3) should be(
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n"
        )
      }
      "have scalable cells" in {
        field1.cells(0, 1, 1) should be("¦-¦-¦-¦-¦\n")
        field1.cells(0, 5, 1) should be("¦  -  ¦  -  ¦  -  ¦  -  ¦\n")
        field2.cells(0, 3, 1) should be("¦ - ¦ - ¦ - ¦ - ¦ - ¦\n")
        field3.cells(0, 7, 2) should be(
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
      "return correct size" in {
        field.rowSize should be(4)
        field.colSize should be(5)
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
        val cellsToCheckDown: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareCase.DownCase, 0, 0)
        cellsToCheckDown should be(Vector((1, 1, 0), (2, 0, 0), (2, 0, 1)))

        val cellsToCheckUp: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareCase.UpCase, 3, 0)
        cellsToCheckUp should be(Vector((1, 2, 0), (2, 2, 0), (2, 2, 1)))
        
        val cellsToCheckRight: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareCase.RightCase, 0, 0)
        cellsToCheckRight should be(Vector((2, 0, 1), (1, 0, 0), (1, 1, 0)))

        val cellsToCheckLeft: Vector[(Int, Int, Int)] = field.cellsToCheck(SquareCase.LeftCase, 0, 4)
        cellsToCheckLeft should be(Vector((2, 0, 3), (1, 0, 3), (1, 1, 3)))
      }
      "return boolean for neighbor cells" in {
        val checkCellsDown: Vector[Boolean] = field.checkAllCells(SquareCase.DownCase, 0, 0)
        checkCellsDown should be(Vector(false, false, false))

        val newField: FieldInterface = field.putRow(1, 0, true).putCol(0, 0, true)

        val newCheckCellsDown: Vector[Boolean] = newField.asInstanceOf[Field].checkAllCells(SquareCase.DownCase, 0, 0)
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

        field.putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true).putCol(0, 1, true).checkSquare(SquareCase.RightCase, 0, 0).toString should be(
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
      "do a move in mid case" in {
        val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

        field.checkSquare(SquareCase.DownCase, 1, 1) should be(field)
        field.putRow(1, 1, true).putRow(2, 1, true).putCol(1, 1, true).putCol(1, 2, true).checkSquare(SquareCase.DownCase, 1, 1).toString should be(
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
      "give access to player" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Four, PlayerType.Human)
        field.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
        field.nextPlayer.currentPlayer shouldBe Player("Red", 0, Status.Red, PlayerType.Human)
      }
      "return information about current game state" in {
        var field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

        val matrixVector1 =
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two,
            PlayerType.Human
          )

        val matrixVector2 =
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Red", 0, Status.Red, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two,
            PlayerType.Human
          )

        val matrixVector3 =
          Matrix(
            Vector(Vector(Status.Blue)),
            Vector(Vector(true), Vector(true)),
            Vector(Vector(true, true)),
            Vector(Player("Blue", 1, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 1, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two,
            PlayerType.Human
          )

        val matrixVector4 =
          Matrix(
            Vector(Vector(Status.Blue)),
            Vector(Vector(true), Vector(true)),
            Vector(Vector(true, true)),
            Vector(Player("Blue", 1, Status.Blue, PlayerType.Human), Player("Red", 5, Status.Red, PlayerType.Human)),
            Player("Red", 5, Status.Red, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two,
            PlayerType.Human
          )

        field.currentPlayer.playerId should be("Blue")
        field.currentPlayerIndex should be(0)
        field.currentPlayerIndex should be(0)
        field.currentStatus should be(
          Vector(
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
          )
        )
        field.currentPlayer.points should be(0)

        field.isFinished shouldBe false

        field = field.putStatus(0, 0, Status.Blue).putRow(0, 0, true).putCol(0, 0, true).putRow(1, 0, true).putCol(0, 1, true)
        field.currentPlayer.playerId should be("Blue")
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

        field = field.addPoints(field.currentPlayerIndex, 1).updatePlayer(field.currentPlayerIndex)
        field.currentPlayer.points should be(1)

        field = field.addPoints(1, 5).updatePlayer(1)
        field.currentPlayer.points should be(5)

        field.getPlayerPoints(0) should be(1)
        field.getPlayerPoints(1) should be(5)

        field.isFinished shouldBe true

        field.asInstanceOf[Field].winner should be("Player Red wins!")

        field.asInstanceOf[Field].stats should be(
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
            PlayerSize.Two,
            PlayerType.Human
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
            PlayerSize.Two,
            PlayerType.Human
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
    "getting field information" should {
      "return correct boardSize" in {
        val fieldSmall = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        fieldSmall.boardSize shouldBe BoardSize.Small

        val fieldMedium = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
        fieldMedium.boardSize shouldBe BoardSize.Medium

        val fieldLarge = new Field(BoardSize.Large, Status.Empty, PlayerSize.Two, PlayerType.Human)
        fieldLarge.boardSize shouldBe BoardSize.Large
      }
      "return correct playerSize" in {
        val fieldTwoPlayer = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
        fieldTwoPlayer.playerSize shouldBe PlayerSize.Two

        val fieldThreePlayer = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Three, PlayerType.Human)
        fieldThreePlayer.playerSize shouldBe PlayerSize.Three

        val fieldFourPlayer = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Four, PlayerType.Human)
        fieldFourPlayer.playerSize shouldBe PlayerSize.Four
      }
      "return correct playerType" in {
        val fieldHumanPlayer = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
        fieldHumanPlayer.playerType shouldBe PlayerType.Human

        val fieldComputerPlayer = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Computer)
        fieldComputerPlayer.playerType shouldBe PlayerType.Computer
      }
      "return correct gameBoardData" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val gameBoardData: GameBoardData = field.gameBoardData
        gameBoardData.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
        gameBoardData.statusCells shouldBe Vector(Vector("-", "-", "-", "-"), Vector("-", "-", "-", "-"), Vector("-", "-", "-", "-"))
        gameBoardData.rowCells shouldBe Vector(Vector(false, false, false, false), Vector(false, false, false, false), Vector(false, false, false, false), Vector(false, false, false, false))
        gameBoardData.colCells shouldBe Vector(Vector(false, false, false, false, false), Vector(false, false, false, false, false), Vector(false, false, false, false, false))
      }
      "return correct fieldData" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val fieldData: FieldData = field.fieldData(ComputerDifficulty.Easy)
        fieldData.boardSize shouldBe BoardSize.Small
        fieldData.playerSize shouldBe PlayerSize.Two
        fieldData.playerType shouldBe PlayerType.Human
        fieldData.computerDifficulty shouldBe ComputerDifficulty.Easy
        field.fieldData(ComputerDifficulty.Hard).computerDifficulty shouldBe ComputerDifficulty.Hard
      }
      "return correct playerGameData" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val playerGameData: PlayerGameData = field.playerGameData
        playerGameData.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
        playerGameData.winner shouldBe "It's a draw!"
        playerGameData.stats shouldBe "Player Blue [points: 0]\nPlayer Red [points: 0]"
        playerGameData.playerList shouldBe Vector( Player("Blue", 0, Status.Blue, PlayerType.Human),  Player("Red", 0, Status.Red, PlayerType.Human))
      }
      "return correct fieldSizeData" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val fieldSizeData: FieldSizeData = field.fieldSizeData
        fieldSizeData.colSize shouldBe 5
        fieldSizeData.rowSize shouldBe 4
      }
      "return correct unoccupied row coords" in {
        val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.getUnoccupiedRowCoords shouldBe Vector((1, 0, 0), (1, 0, 1), (1, 0, 2), (1, 0, 3), (1, 0, 4), (1, 1, 0),
          (1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 2, 0), (1, 2, 1), (1, 2, 2), (1, 2, 3), (1, 2, 4), (1, 3, 0), 
          (1, 3, 1), (1, 3, 2), (1, 3, 3), (1, 3, 4), (1, 4, 0), (1, 4, 1), (1, 4, 2), (1, 4, 3), (1, 4, 4))

        field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true).putRow(0, 4, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true).putRow(1, 4, true)
          .putRow(2, 0, true).putRow(2, 1, true).putRow(2, 2, true).putRow(2, 3, true).putRow(2, 4, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true).putRow(3, 4, true)
          .getUnoccupiedRowCoords shouldBe Vector((1, 4, 0), (1, 4, 1), (1, 4, 2), (1, 4, 3), (1, 4, 4))
      }
      "return correct unoccupied col coords" in {
        val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.getUnoccupiedColCoords shouldBe Vector((2, 0, 0), (2, 0, 1), (2, 0, 2), (2, 0, 3), (2, 0, 4), (2, 0, 5),
        (2, 1, 0), (2, 1, 1), (2, 1, 2), (2, 1, 3), (2, 1, 4), (2, 1, 5), (2, 2, 0), (2, 2, 1), (2, 2, 2), (2, 2, 3),
        (2, 2, 4), (2, 2, 5), (2, 3, 0), (2, 3, 1), (2, 3, 2), (2, 3, 3), (2, 3, 4), (2, 3, 5))

        field
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 3, true).putCol(0, 4, true).putCol(0, 5, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 2, true).putCol(1, 3, true).putCol(1, 4, true).putCol(1, 5, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 2, true).putCol(2, 3, true).putCol(2, 4, true).putCol(2, 5, true)
          .getUnoccupiedColCoords shouldBe Vector((2, 3, 0), (2, 3, 1), (2, 3, 2), (2, 3, 3), (2, 3, 4), (2, 3, 5))
      }
    }
    "getting moves" should {
      "return winning moves" in {
        val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.getWinningMoves(field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords, field) shouldBe Vector()

        val newField = field.putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true)
        newField.getWinningMoves(newField.getUnoccupiedRowCoords ++ newField.getUnoccupiedColCoords, newField) shouldBe Vector(Move(2, 0, 1, true))

        val newField2 = field
          .putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true)
          .putRow(2, 0, true).putCol(1, 0, true)
        newField2.getWinningMoves(newField2.getUnoccupiedRowCoords ++ newField2.getUnoccupiedColCoords, newField2) shouldBe Vector(Move(2, 0, 1, true), Move(2, 1, 1, true))
      }
      "return save moves" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val allMoves: Vector[Move] = (field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords).map(coord => Move(coord._1, coord._2, coord._3, true))
        field.getSaveMoves(field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords, field) shouldBe allMoves

        val newField: FieldInterface = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 2, true).putCol(1, 3, true).putCol(1, 4, true)
        val saveMoves: Vector[Move] = newField.getSaveMoves(newField.getUnoccupiedRowCoords ++ newField.getUnoccupiedColCoords, newField)
        saveMoves shouldBe Vector(
          Move(1, 3, 0, true), Move(1, 3, 1, true), Move(1, 3, 2, true), Move(1, 3, 3, true),
          Move(2, 2, 0, true), Move(2, 2, 1, true), Move(2, 2, 2, true), Move(2, 2, 3, true), Move(2, 2, 4, true)
        )
      }
      "return missing moves" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.getMissingMoves(1, 0, 0, field) shouldBe Vector()

        val newField = field.putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true)
        newField.getMissingMoves(1, 0, 0, newField) shouldBe Vector((2, 0, 1))
      }
    }
    "checking square cases" should {
      "return correct single square case" in {
        val field: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.squareCases(1, 0, 0, field).head shouldBe SquareCase.DownCase
        field.squareCases(1, 3, 0, field).head shouldBe SquareCase.UpCase
        field.squareCases(2, 0, 0, field).head shouldBe SquareCase.RightCase
        field.squareCases(2, 0, 4, field).head shouldBe SquareCase.LeftCase
      }
      "return correct multiple square cases" in {
        val field: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.squareCases(1, 1, 1, field) shouldBe Vector(SquareCase.DownCase, SquareCase.UpCase)
        field.squareCases(2, 0, 2, field) shouldBe Vector(SquareCase.RightCase, SquareCase.LeftCase)
      }
      "check all cells in square cases" in {
        val field: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.checkAllCells(SquareCase.DownCase, 0, 0) shouldBe Vector(false, false, false)

        field.putRow(1, 0, true).asInstanceOf[Field].checkAllCells(SquareCase.DownCase, 0, 0) shouldBe Vector(true, false, false)
      }
      "return all cells to check" in {
        val field: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.cellsToCheck(SquareCase.DownCase, 0, 0) shouldBe Vector((1, 1, 0), (2, 0, 0), (2, 0, 1))
      }
    }
    "checking for moves" should {
      "return if it's a closing move" in {
        val field: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.isClosingMove(1, 0, 0, field) shouldBe false
        val newField = field.putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true).asInstanceOf[Field]
        newField.isClosingMove(2, 0, 1, newField) shouldBe true
      }
      "return if it's a risky move" in {
        val field: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        field.isRiskyMove(1, 0, 0, field) shouldBe false
        val newField = field.putRow(0, 0, true).putRow(1, 0, true).asInstanceOf[Field]
        newField.isRiskyMove(2, 0, 0, newField) shouldBe true
        newField.isRiskyMove(2, 0, 1, newField) shouldBe true
      }
    }
    "evaluating chains" should {
      val chainedField: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        .putRow(1, 1, true)
        .putRow(1, 2, true)
        .putRow(2, 2, true)
        .putRow(2, 3, true)
        .putCol(0, 0, true)
        .putCol(0, 1, true)
        .putCol(0, 3, true)
        .putCol(0, 4, true)
        .putCol(1, 0, true)
        .putCol(1, 1, true)
        .putCol(1, 4, true)
        .putCol(2, 0, true)
        .putCol(2, 1, true)
        .putCol(2, 2, true)
        .putCol(2, 4, true).asInstanceOf[Field]
      "evaluate all chain moves and points outcome for given move" in {
        val chainMovesWithPoints: (Int, Vector[(Int, Int, Int)]) =
          chainedField.evaluateChainWithPointsOutcome((1, 0, 0), chainedField)

        chainMovesWithPoints._1 shouldBe 3
        chainMovesWithPoints._2.size shouldBe 4
        chainMovesWithPoints._2 should be(
          Vector(
            (1, 0, 0),
            (1, 1, 0),
            (1, 2, 0),
            (1, 3, 0)
          )
        )
      }
      "evaluate all chains for available moves" in {
        val allAvailableMoves: Vector[(Int, Int, Int)] =
          chainedField.getUnoccupiedRowCoords ++ chainedField.getUnoccupiedColCoords

        val allPossibleChains: Vector[(Int, Vector[(Int, Int, Int)])] = allAvailableMoves
          .map(chainedField.evaluateChainWithPointsOutcome(_, chainedField))

        val distinctChains: Vector[(Int, Set[(Int, Int, Int)])] = allPossibleChains
          .map { case (x, innerVec) => (x, innerVec.map { case (vec, x, y) => (vec, x, y) }.toSet) }
          .distinctBy(_._2)

        distinctChains.size shouldBe 4
        distinctChains(0)._1 shouldBe 3
        distinctChains(1)._1 shouldBe 2
        distinctChains(2)._1 shouldBe 5
        distinctChains(3)._1 shouldBe 2

        allPossibleChains should contain((3, Vector((1, 0, 0), (1, 1, 0), (1, 2, 0), (1, 3, 0))))
        allPossibleChains should contain((2, Vector((1, 0, 1), (2, 0, 2), (1, 0, 2))))
        allPossibleChains should contain((5, Vector((1, 0, 3), (1, 1, 3), (2, 1, 3), (2, 1, 2), (1, 2, 1), (1, 3, 1))))
        allPossibleChains should contain((2, Vector((1, 3, 2), (2, 2, 3), (1, 3, 3))))
      }
      "evaluate nothing on empty field" in {
        val field: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val allAvailableMoves: Vector[(Int, Int, Int)] =
          field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords

        val emptyEval = allAvailableMoves
          .map(field.evaluateChainWithPointsOutcome(_, field))
          .filterNot(_._1 == 0)

        emptyEval.size shouldBe 0
      }
      "evaluate one point for a winning move" in {
        val winningField = chainedField.putRow(0, 0, true).putRow(0, 1, true).asInstanceOf[Field]

        val point1 = winningField.evaluatePointsOutcome(1, 1, 0, winningField)
        val point2 = winningField.evaluatePointsOutcome(2, 0, 2, winningField)
        val noPoint = winningField.evaluatePointsOutcome(1, 0, 2, winningField)

        point1 shouldBe 1
        point2 shouldBe 1
        noPoint shouldBe 0
      }
      "evaluate two points for a winning move" in {
        val winningField = chainedField.putRow(0, 0, true).putRow(2, 0, true).putRow(0, 1, true).putRow(0, 2, true).asInstanceOf[Field]

        val twoPoints1 = winningField.evaluatePointsOutcome(1, 1, 0, winningField)
        val twoPoints2 = winningField.evaluatePointsOutcome(2, 0, 2, winningField)

        twoPoints1 shouldBe 2
        twoPoints2 shouldBe 2
      }
      "evaluate all chains with points outcome" in {
        val allAvailableCoords: Vector[(Int, Int, Int)] =
          chainedField.getUnoccupiedRowCoords ++ chainedField.getUnoccupiedColCoords

        chainedField.chainsWithPointsOutcome(allAvailableCoords, chainedField) shouldBe Vector(
          (3, Vector((1, 0, 0), (1, 1, 0), (1, 2, 0), (1, 3, 0))),
          (2, Vector((1, 0, 1), (2, 0, 2), (1, 0, 2))),
          (2, Vector((1, 0, 2), (2, 0, 2), (1, 0, 1))),
          (5, Vector((1, 0, 3), (1, 1, 3), (2, 1, 3), (2, 1, 2), (1, 2, 1), (1, 3, 1))),
          (3, Vector((1, 1, 0), (1, 0, 0), (1, 2, 0), (1, 3, 0))),
          (5, Vector((1, 1, 3), (1, 0, 3), (2, 1, 3), (2, 1, 2), (1, 2, 1), (1, 3, 1))),
          (3, Vector((1, 2, 0), (1, 1, 0), (1, 0, 0), (1, 3, 0))),
          (5, Vector((1, 2, 1), (2, 1, 2), (2, 1, 3), (1, 1, 3), (1, 0, 3), (1, 3, 1))),
          (3, Vector((1, 3, 0), (1, 2, 0), (1, 1, 0), (1, 0, 0))),
          (5, Vector((1, 3, 1), (1, 2, 1), (2, 1, 2), (2, 1, 3), (1, 1, 3), (1, 0, 3))),
          (2, Vector((1, 3, 2), (2, 2, 3), (1, 3, 3))),
          (2, Vector((1, 3, 3), (2, 2, 3), (1, 3, 2))),
          (2, Vector((2, 0, 2), (1, 0, 1), (1, 0, 2))),
          (5, Vector((2, 1, 2), (1, 2, 1), (1, 3, 1), (2, 1, 3), (1, 1, 3), (1, 0, 3))),
          (5, Vector((2, 1, 3), (2, 1, 2), (1, 2, 1), (1, 3, 1), (1, 1, 3), (1, 0, 3))),
          (2, Vector((2, 2, 3), (1, 3, 2), (1, 3, 3)))
        )
      }
      "recognize a circular sequence in a 3x2 circle" in {
        val circularField: Field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(0, 0, true)
          .putRow(0, 1, true)
          .putRow(0, 2, true)
          .putRow(2, 0, true)
          .putRow(2, 1, true)
          .putRow(2, 2, true)
          .putRow(1, 1, true)
          .putCol(0, 0, true)
          .putCol(0, 3, true)
          .putCol(1, 0, true)
          .putCol(1, 3, true).asInstanceOf[Field]

        val circularMoves1: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 2))
        val circularMoves2: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (2, 0, 1))
        val circularMoves3: Vector[(Int, Int, Int)] = Vector((2, 0, 1), (2, 1, 2))
        val notCircularMoves: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 3))

        val circular1: Boolean = circularField
          .isCircularSequence(
            circularField.evaluateChainWithPointsOutcome(circularMoves1.head, circularField),
            circularField.evaluateChainWithPointsOutcome(circularMoves1.last, circularField)
          )
        val circular2: Boolean = circularField
          .isCircularSequence(
            circularField.evaluateChainWithPointsOutcome(circularMoves2.head, circularField),
            circularField.evaluateChainWithPointsOutcome(circularMoves2.last, circularField)
          )
        val circular3: Boolean = circularField
          .isCircularSequence(
            circularField.evaluateChainWithPointsOutcome(circularMoves3.head, circularField),
            circularField.evaluateChainWithPointsOutcome(circularMoves3.last, circularField)
          )
        val notCircular: Boolean = circularField
          .isCircularSequence(
            circularField.evaluateChainWithPointsOutcome(notCircularMoves.head, circularField),
            circularField.evaluateChainWithPointsOutcome(notCircularMoves.last, circularField)
          )

        circular1 shouldBe true
        circular2 shouldBe true
        circular3 shouldBe true
        notCircular shouldBe false
      }
    }
    "creating a new field" should {
      "return a correct field" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val newField = field.newField(BoardSize.Large, Status.Blue, PlayerSize.Four, PlayerType.Computer)
        newField.boardSize shouldBe BoardSize.Large
        newField.playerSize shouldBe PlayerSize.Four
        newField.playerType shouldBe PlayerType.Computer
        newField.getStatusCell(0, 0) shouldBe Status.Blue
      }
    }
  }
}
