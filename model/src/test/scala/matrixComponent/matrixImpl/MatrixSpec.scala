package model
package matrixComponent.matrixImpl

import de.htwg.se.dotsandboxes.util.Move
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.dotsandboxes.util.moveState.SquareCases
import de.htwg.se.dotsandboxes.util.{BoardSize, PlayerSize, PlayerType}
import de.htwg.se.dotsandboxes.model.matrixComponent.MatrixInterface

class MatrixSpec extends AnyWordSpec {
  "A Matrix" when {
    "initialized" should {
      "have the correct size" in {
        val matrix1 = new Matrix(
          Vector(Vector(Status.Empty)),
          Vector(Vector(false), Vector(false, true)),
          Vector(Vector(false, true)),
          Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
          list.head,
          BoardSize.Medium,
          PlayerSize.Two
        )
        matrix1.rowSize() should be(2)
        matrix1.colSize() should be(2)

        val matrix2 = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        matrix2.rowSize() should be(4)
        matrix2.colSize() should be(5)
      }
      val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return proper vectors" in {
        val expectedVectorStatus: Vector[Vector[Status]] =
          Vector(
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
          )
        val expectedVectorRow: Vector[Vector[Boolean]] =
          Vector(
            Vector(false, false, false, false),
            Vector(false, false, false, false),
            Vector(false, false, false, false),
            Vector(false, false, false, false)
          )
        val expectedVectorCol: Vector[Vector[Boolean]] =
          Vector(
            Vector(false, false, false, false, false),
            Vector(false, false, false, false, false),
            Vector(false, false, false, false, false)
          )
        matrix.vectorStatus should be(expectedVectorStatus)
        matrix.vectorRow should be(expectedVectorRow)
        matrix.vectorCol should be(expectedVectorCol)
      }
      "return proper max positions" in {
        matrix.maxPosX should be(3)
        matrix.maxPosY should be(4)
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
    "return boardSize" in {
      val matrix = new Matrix(BoardSize.Large, Status.Empty, PlayerSize.Two, PlayerType.Human)
      matrix.boardSize shouldBe BoardSize.Large
    }
    "return player size" in {
      val matrix = new Matrix(BoardSize.Large, Status.Empty, PlayerSize.Three, PlayerType.Human)
      matrix.playerSize shouldBe PlayerSize.Three
    }
    "checking for squares" should {
      val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val matrix2 = matrix
        .replaceRowCell(0, 0, true)
        .replaceRowCell(1, 0, true)
        .replaceColCell(0, 0, true)
        .replaceColCell(0, 1, true)

      "return same matrix" in {
        val matrixVector =
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            list.head,
            BoardSize.Small,
            PlayerSize.Two
          )

        matrix.checkSquare(SquareCases.UpCase, 1, 0) should be(matrixVector)
        matrix.checkSquare(SquareCases.RightCase, 0, 0) should be(matrixVector)
        matrix.checkSquare(SquareCases.LeftCase, 0, 1) should be(matrixVector)
      }
      "return correct matrix on edge case" in {
        val matrixVector =
          Matrix(
            Vector(
              Vector(Status.Blue, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(true, false, false, false),
              Vector(true, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(true, true, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            list.head,
            BoardSize.Small,
            PlayerSize.Two
          )

        matrix2.checkSquare(SquareCases.DownCase, 0, 0) should be(matrixVector)
        matrix2.checkSquare(SquareCases.UpCase, 1, 0) should be(matrixVector)

        matrix2.checkSquare(SquareCases.RightCase, 0, 0) should be(matrixVector)
        matrix2.checkSquare(SquareCases.LeftCase, 0, 1) should be(matrixVector)
      }
    }
    "checking a move" should {
      "return correct matrix" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val matrix2 = matrix
          .replaceRowCell(0, 0, true)
          .replaceRowCell(1, 0, true)
          .replaceColCell(0, 0, true)
          .replaceColCell(0, 1, true)
        val matrixVector =
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(true, false, false, false),
              Vector(true, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(true, true, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            list.head,
            BoardSize.Small,
            PlayerSize.Two
          )
        val matrixVector2 =
          Matrix(
            Vector(
              Vector(Status.Blue, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(true, false, false, false),
              Vector(true, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(true, true, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            list.head,
            BoardSize.Small,
            PlayerSize.Two
          )

        matrix2.checkSquare(SquareCases.DownCase, 0, 0) should be(matrixVector2)
        matrix2.checkSquare(SquareCases.UpCase, matrix2.maxPosX, 0) should be(matrixVector)

        matrix2.checkSquare(SquareCases.RightCase, 0, 0) should be(matrixVector2)
        matrix2.checkSquare(SquareCases.LeftCase, 0, matrix2.maxPosY) should be(matrixVector)
      }
    }
    "checking for edge case" should {
      "return correct boolean for a move" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
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
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

        matrix.currentPlayerInfo._1 should be("Blue")
        matrix.currentPlayerInfo._2 should be(0)
        matrix.updatePlayer(0).currentPlayerInfo._2 should be(0)
        matrix.updatePlayer(0).changePlayer.currentPlayerInfo._2 should be(1)
      }
      "return correct player points" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

        matrix.currentPlayerInfo(0) should be("Blue")
        matrix.currentPoints should be(0)

        matrix.addPoints(0, 1).updatePlayer(0).currentPoints should be(1)
        matrix.addPoints(1, 5).updatePlayer(1).currentPoints should be(5)
        matrix
          .addPoints(1, 5).updatePlayer(1)
          .addPoints(1, 3).updatePlayer(1)
          .currentPoints should be(8)
      }
      "return cells to check" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val cellsToCheckDown: Vector[(Int, Int, Int)] = matrix.cellsToCheck(SquareCases.DownCase, 0, 0)
        cellsToCheckDown should be(Vector((1, 1, 0), (2, 0, 0), (2, 0, 1)))

        val cellsToCheckUp: Vector[(Int, Int, Int)] = matrix.cellsToCheck(SquareCases.UpCase, 3, 0)
        cellsToCheckUp should be(Vector((1, 2, 0), (2, 2, 0), (2, 2, 1)))
        
        val cellsToCheckRight: Vector[(Int, Int, Int)] = matrix.cellsToCheck(SquareCases.RightCase, 0, 0)
        cellsToCheckRight should be(Vector((2, 0, 1), (1, 0, 0), (1, 1, 0)))

        val cellsToCheckLeft: Vector[(Int, Int, Int)] = matrix.cellsToCheck(SquareCases.LeftCase, 0, 4)
        cellsToCheckLeft should be(Vector((2, 0, 3), (1, 0, 3), (1, 1, 3)))
      }
      "return boolean for neighbor cells" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val checkCellsDown: Vector[Boolean] = matrix.checkAllCells(SquareCases.DownCase, 0, 0)
        checkCellsDown should be(Vector(false, false, false))

        val newMatrix: MatrixInterface = matrix.replaceRowCell(1, 0, true).replaceColCell(0, 0, true)

        val newCheckCellsDown: Vector[Boolean] = newMatrix.checkAllCells(SquareCases.DownCase, 0, 0)
        newCheckCellsDown should be(Vector(true, true, false))
      }
      "return unoccupied row coordinates" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .replaceRowCell(0, 0, true).replaceRowCell(0, 1, true).replaceRowCell(0, 2, true)
          .replaceRowCell(1, 0, true).replaceRowCell(1, 1, true).replaceRowCell(1, 2, true).replaceRowCell(1, 3, true)
          .replaceRowCell(2, 0, true).replaceRowCell(2, 1, true).replaceRowCell(2, 2, true).replaceRowCell(2, 3, true)

        matrix.getUnoccupiedRowCoord() shouldBe Vector(
          (1, 0, 3), (1, 3, 0), (1, 3, 1), (1, 3, 2), (1, 3, 3)
        )

        val newMatrix = matrix.replaceRowCell(0, 3, true).replaceRowCell(3, 0, true).replaceRowCell(3, 1, true)

        newMatrix.getUnoccupiedRowCoord() shouldBe Vector(
          (1, 3, 2), (1, 3, 3)
        )
      }
      "return unoccupied column coordinates" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .replaceColCell(0, 0, true).replaceColCell(0, 1, true).replaceColCell(0, 2, true).replaceColCell(0, 4, true)
          .replaceColCell(1, 0, true).replaceColCell(1, 1, true).replaceColCell(1, 2, true).replaceColCell(1, 3, true).replaceColCell(1, 4, true)

        matrix.getUnoccupiedColCoord() shouldBe Vector(
          (2, 0, 3), (2, 2, 0), (2, 2, 1), (2, 2, 2), (2, 2, 3), (2, 2, 4)
        )

        val newMatrix = matrix.replaceColCell(0, 3, true).replaceColCell(2, 0, true).replaceColCell(2, 1, true)

        newMatrix.getUnoccupiedColCoord() shouldBe Vector(
          (2, 2, 2), (2, 2, 3), (2, 2, 4)
        )
      }
      "return current player" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        matrix.getCurrentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
      }
      "return a matrix with correct player" in {
        val matrix = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

        val matrixVector =
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Red", 0, Status.Red, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )

        val matrixVector2 =
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )

        matrix.changePlayer should be(matrixVector)
        matrix.updatePlayer(0) should be(matrixVector2)

        matrix.changePlayer.changePlayer should be(matrixVector2)
        matrix.updatePlayer(0) should be(matrixVector2)

        matrix.updatePlayer(1) should be(matrixVector)

        matrix.list should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)))
        matrix.addPoints(0, 1).updatePlayer(0).list should be(Vector(Player("Blue", 1, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)))

        matrix.addPoints(0, 1).updatePlayer(0) should be(
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 1, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 1, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )
        )

        matrix.currentPlayer should be(Player("Blue", 0, Status.Blue, PlayerType.Human))
        matrix.changePlayer.addPoints(1, 1).updatePlayer(1).currentPlayer should be(Player("Red", 1, Status.Red, PlayerType.Human))

        matrix.changePlayer.addPoints(1, 1).updatePlayer(1) should be(
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 1, Status.Red, PlayerType.Human)),
            Player("Red", 1, Status.Red, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )
        )

        matrix.addPoints(0, 3).updatePlayer(0) should be(
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 3, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 3, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )
        )

        matrix.addPoints(1, 5).updatePlayer(1).getPoints(1) should be(5)

        matrix.list should not be (list)

        val matrix2 = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val matrix3 = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Human)
        val matrix4 = new Matrix(BoardSize.Small, Status.Empty, PlayerSize.Four, PlayerType.Human)

        matrix2 should be(
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)),
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Two
          )
        )

        matrix3 should be(
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human)),
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Three
          )
        )

        matrix4 should be(
          Matrix(
            Vector(
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty),
              Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty)
            ),
            Vector(
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false),
              Vector(false, false, false, false)
            ),
            Vector(
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false),
              Vector(false, false, false, false, false)
            ),
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human), Player("Yellow", 0, Status.Yellow, PlayerType.Human)),
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            BoardSize.Small,
            PlayerSize.Four
          )
        )
      }
    }
  }
}
