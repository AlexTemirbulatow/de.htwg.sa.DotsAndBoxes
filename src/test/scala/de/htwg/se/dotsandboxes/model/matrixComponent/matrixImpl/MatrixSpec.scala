package de.htwg.se.dotsandboxes.model
package matrixComponent.matrixImpl

import de.htwg.se.dotsandboxes.util.Move
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.dotsandboxes.util.moveState.SquareState

class MatrixSpec extends AnyWordSpec {
  "A Matrix" when {
    "initialized" should {
      "have the correct size" in {
        val matrix1 = new Matrix(
          Vector(Vector(Status.Empty)),
          Vector(Vector(false), Vector(false, true)),
          Vector(Vector(false, true)),
          Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
          list.head
        )
        matrix1.rowSize() should be(2)
        matrix1.colSize() should be(2)

        val matrix2 = new Matrix(3, 2, Status.Empty)
        matrix2.rowSize() should be(3)
        matrix2.colSize() should be(4)
      }
      val matrix = new Matrix(2, 2, Status.Empty, 2)
      "return proper vectors" in {
        val expectedVectorStatus: Vector[Vector[Status]] =
          Vector(
            Vector(Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty)
          )
        val expectedVectorRow: Vector[Vector[Boolean]] =
          Vector(
            Vector(false, false),
            Vector(false, false),
            Vector(false, false)
          )
        val expectedVectorCol: Vector[Vector[Boolean]] =
          Vector(
            Vector(false, false, false),
            Vector(false, false, false),
          )
        matrix.vectorStatus should be(expectedVectorStatus)
        matrix.vectorRow should be(expectedVectorRow)
        matrix.vectorCol should be(expectedVectorCol)
      }
      "return proper max positions" in {
        matrix.maxPosX should be(2)
        matrix.maxPosY should be(2)
      }
      "give access to its status cells" in {
        matrix.statusCell(0, 0) should be(Status.Empty)
        matrix.statusCell(0, 1) should be(Status.Empty)
        matrix.statusCell(1, 0) should be(Status.Empty)
        matrix.statusCell(1, 1) should be(Status.Empty)
      }
      "give access to its row cells" in {
        matrix.rowCell(0, 0) shouldBe false
        matrix.rowCell(0, 1) shouldBe false
        matrix.rowCell(1, 0) shouldBe false
        matrix.rowCell(1, 1) shouldBe false
      }
      "give access to its col cells" in {
        matrix.colCell(0, 0) shouldBe false
        matrix.colCell(0, 1) shouldBe false
        matrix.colCell(1, 0) shouldBe false
        matrix.colCell(1, 1) shouldBe false
      }
      "allow to replace status cells" in {
        val replMatrix = matrix
          .replaceStatusCell(0, 0, Status.Blue)
          .replaceStatusCell(0, 1, Status.Red)
          .replaceStatusCell(1, 0, Status.Red)

        matrix.statusCell(0, 0) should be(Status.Empty)
        matrix.statusCell(0, 1) should be(Status.Empty)
        matrix.statusCell(1, 0) should be(Status.Empty)

        replMatrix.statusCell(0, 0) should be(Status.Blue)
        replMatrix.statusCell(0, 1) should be(Status.Red)
        replMatrix.statusCell(1, 0) should be(Status.Red)
      }
      "allow to replace row cells" in {
        val replMatrix = matrix
          .replaceRowCell(0, 0, true)
          .replaceRowCell(0, 1, true)
          .replaceRowCell(1, 1, true)
          .replaceRowCell(1, 1, false)

        matrix.rowCell(0, 0) shouldBe false
        matrix.rowCell(0, 1) shouldBe false
        matrix.rowCell(1, 1) shouldBe false

        replMatrix.rowCell(0, 0) shouldBe true
        replMatrix.rowCell(0, 1) shouldBe true
        replMatrix.rowCell(1, 1) shouldBe false
      }
      "allow to replace col cells" in {
        val replMatrix = matrix
          .replaceColCell(0, 0, true)
          .replaceColCell(0, 1, true)
          .replaceColCell(1, 1, true)
          .replaceColCell(1, 1, false)

        matrix.colCell(0, 0) shouldBe false
        matrix.colCell(0, 1) shouldBe false
        matrix.rowCell(1, 1) shouldBe false

        replMatrix.colCell(0, 0) shouldBe true
        replMatrix.colCell(0, 1) shouldBe true
        replMatrix.colCell(1, 1) shouldBe false
      }
    }
    "checking for squares" should {
      val matrix = new Matrix(2, 2, Status.Empty)
      val matrix2 = matrix
        .replaceRowCell(0, 0, true)
        .replaceRowCell(1, 0, true)
        .replaceColCell(0, 0, true)
        .replaceColCell(0, 1, true)

      "return same matrix" in {
        val matrixVector =
          Matrix(
            Vector(Vector(Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty)),
            Vector(Vector(false, false), Vector(false, false), Vector(false, false)),
            Vector(Vector(false, false, false), Vector(false, false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            list.head
          )

        matrix.checkSquare(SquareState.UpCase, 1, 0) should be(matrixVector)
        matrix.checkSquare(SquareState.RightCase, 0, 0) should be(matrixVector)
        matrix.checkSquare(SquareState.LeftCase, 0, 1) should be(matrixVector)
      }
      "return correct matrix on edge case" in {
        val matrixVector =
          Matrix(
            Vector(Vector(Status.Blue, Status.Empty), Vector(Status.Empty, Status.Empty)),
            Vector(Vector(true, false), Vector(true, false), Vector(false, false)),
            Vector(Vector(true, true, false), Vector(false, false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            list.head
          )

        matrix2.checkSquare(SquareState.DownCase, 0, 0) should be(matrixVector)
        matrix2.checkSquare(SquareState.UpCase, 1, 0) should be(matrixVector)

        matrix2.checkSquare(SquareState.RightCase, 0, 0) should be(matrixVector)
        matrix2.checkSquare(SquareState.LeftCase, 0, 1) should be(matrixVector)
      }
    }
    "checking a move" should {
      "return correct matrix" in {
        val matrix = new Matrix(2, 2, Status.Empty)
        val matrix2 = matrix
          .replaceRowCell(0, 0, true)
          .replaceRowCell(1, 0, true)
          .replaceColCell(0, 0, true)
          .replaceColCell(0, 1, true)
        val matrixVector =
          Matrix(
            Vector(Vector(Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty)),
            Vector(Vector(true, false), Vector(true, false), Vector(false, false)),
            Vector(Vector(true, true, false), Vector(false, false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            list.head
          )

        val matrixVector2 =
          Matrix(
            Vector(Vector(Status.Blue, Status.Empty), Vector(Status.Empty, Status.Empty)),
            Vector(Vector(true, false), Vector(true, false), Vector(false, false)),
            Vector(Vector(true, true, false), Vector(false, false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            list.head
          )

        matrix2.checkSquare(SquareState.DownCase, 0, 0) should be(matrixVector2)
        matrix2.checkSquare(SquareState.UpCase, matrix2.maxPosX, 0) should be(matrixVector)

        matrix2.checkSquare(SquareState.RightCase, 0, 0) should be(matrixVector2)
        matrix2.checkSquare(SquareState.LeftCase, 0, matrix2.maxPosY) should be(matrixVector)
      }
    }
    "checking for edge case" should {
      "return correct boolean for a move" in {
        val matrix = new Matrix(3, 3, Status.Empty)
        val move1 = new Move(1, 0, 0, true)
        val move2 = new Move(1, 1, 1, true)
        val move3 = new Move(2, 0, 0, true)
        val move4 = new Move(2, 1, 1, true)

        matrix.isEdge(move1) shouldBe true
        matrix.isEdge(move2) shouldBe false

        matrix.isEdge(move3) shouldBe true
        matrix.isEdge(move4) shouldBe false
      }
    }
    "manipulating a player" should {
      "return the correct player" in {
        val matrix = new Matrix(1, 1, Status.Empty)

        matrix.currentPlayerInfo._1 should be("Blue")
        matrix.currentPlayerInfo._2 should be(0)
        matrix.updatePlayer(0).currentPlayerInfo._2 should be(0)
        matrix.updatePlayer(0).changePlayer.currentPlayerInfo._2 should be(1)
      }
      "return correct player points" in {
        val matrix = new Matrix(2, 2, Status.Empty, 2)

        matrix.currentPlayerInfo(0) should be("Blue")
        matrix.currentPoints should be(0)

        matrix.addPoints(0, 1).updatePlayer(0).currentPoints should be(1)
        matrix.addPoints(1, 5).updatePlayer(1).currentPoints should be(5)
        matrix
          .addPoints(1, 5).updatePlayer(1)
          .addPoints(1, 3).updatePlayer(1)
          .currentPoints should be(8)
      }
      "return a matrix with correct player" in {
        val matrix = new Matrix(1, 1, Status.Empty)

        val matrixVector =
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            Player("Red", 0, Status.Red)
          )

        val matrixVector2 =
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            Player("Blue", 0, Status.Blue)
          )

        matrix.changePlayer should be(matrixVector)
        matrix.updatePlayer(0) should be(matrixVector2)

        matrix.changePlayer.changePlayer should be(matrixVector2)
        matrix.updatePlayer(0) should be(matrixVector2)

        matrix.updatePlayer(1) should be(matrixVector)

        matrix.list should be(Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)))
        matrix.addPoints(0, 1).updatePlayer(0).list should be(Vector(Player("Blue", 1, Status.Blue), Player("Red", 0, Status.Red)))

        matrix.addPoints(0, 1).updatePlayer(0) should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 1, Status.Blue), Player("Red", 0, Status.Red)),
            Player("Blue", 1, Status.Blue)
          )
        )

        matrix.currentPlayer should be(Player("Blue", 0, Status.Blue))
        matrix.changePlayer.addPoints(1, 1).updatePlayer(1).currentPlayer should be(Player("Red", 1, Status.Red))

        matrix.changePlayer.addPoints(1, 1).updatePlayer(1) should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 1, Status.Red)),
            Player("Red", 1, Status.Red)
          )
        )

        matrix.addPoints(0, 3).updatePlayer(0) should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 3, Status.Blue), Player("Red", 0, Status.Red)),
            Player("Blue", 3, Status.Blue)
          )
        )

        matrix.addPoints(1, 5).updatePlayer(1).getPoints(1) should be(5)

        matrix.list should not be (list)

        val matrix0 = new Matrix(1, 1, Status.Empty)
        val matrix1 = new Matrix(1, 1, Status.Empty, 1)
        val matrix2 = new Matrix(1, 1, Status.Empty, 2)
        val matrix3 = new Matrix(1, 1, Status.Empty, 3)
        val matrix4 = new Matrix(1, 1, Status.Empty, 4)

        matrix0 should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            Player("Blue", 0, Status.Blue)
          )
        )

        matrix1 should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue)),
            Player("Blue", 0, Status.Blue)
          )
        )

        matrix2 should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red)),
            Player("Blue", 0, Status.Blue)
          )
        )

        matrix3 should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red), Player("Green", 0, Status.Green)),
            Player("Blue", 0, Status.Blue)
          )
        )

        matrix4 should be(
          Matrix(
            Vector(Vector(Status.Empty)),
            Vector(Vector(false), Vector(false)),
            Vector(Vector(false, false)),
            Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red), Player("Green", 0, Status.Green), Player("Yellow", 0, Status.Yellow)),
            Player("Blue", 0, Status.Blue)
          )
        )
      }
    }
  }
}
