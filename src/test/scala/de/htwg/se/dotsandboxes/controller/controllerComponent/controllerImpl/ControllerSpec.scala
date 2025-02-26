package de.htwg.se.dotsandboxes
package controller
package controllerComponent
package controllerImpl

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.Mockito._
import scala.util.Failure

import Default.given
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.fileIoComponent.xmlImpl
import model.matrixComponent.matrixImpl.{Player, Status}
import model.computerComponent.computerEasyImpl.ComputerEasy
import model.computerComponent.computerMediumImpl.ComputerMedium
import model.computerComponent.computerHardImpl.ComputerHard
import model.computerComponent.ComputerInterface
import util.{BoardSize, PlayerSize, PlayerType, PackT, ComputerDifficulty, Event, Observer, Move}

class ControllerSpec extends AnyWordSpec {
  val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
  "The Controller" should {
    "put a connected line on the field when a move is made" in {
      val fieldWithMove = controller.put(Move(1, 0, 0, true))
      fieldWithMove.getRowCell(0, 0) shouldBe true
      fieldWithMove.getRowCell(0, 1) shouldBe false
    }
    "notify its observers on change and update the game" in {
      class TestObserver(controller: Controller) extends Observer:
        controller.add(this)
        var bing = false
        def update(e: Event) = bing = true
      val testObserver = TestObserver(controller)

      controller.toString should be(
        "\n\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Blues turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )

      testObserver.bing shouldBe false
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.gameEnded shouldBe false
      testObserver.bing shouldBe true

      controller.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human)))
      controller.rowSize() should be(4)
      controller.colSize() should be(5)

      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(1, 1, 1, true))
      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Greens turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(2, 0, 1, true))
      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ‖   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ‖   -   ¦   -   ¦   -   ¦\n" +
          "O-------O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Blues turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.currentPlayer should be("Blue")
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.currentPoints should be(1)
      controller.currentPlayer should be("Red")
      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O-------O\n" +
          "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
          "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
          "O=======O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 1]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(1, 0, 1, true))
      controller.publish(controller.put, Move(1, 0, 2, true))
      controller.publish(controller.put, Move(1, 0, 3, true))
      controller.publish(controller.put, Move(1, 1, 2, true))
      controller.publish(controller.put, Move(1, 1, 3, true))
      controller.publish(controller.put, Move(2, 0, 2, true))
      controller.publish(controller.put, Move(2, 0, 4, true))
      controller.publish(controller.put, Move(2, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 3, true))
      controller.toString should be(
        "\n\n" +
          "O=======O=======O=======O=======O\n" +
          "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
          "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
          "O=======O=======O=======O=======O\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Greens turn\n" +
          "[points: 2]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.currentPlayer should be("Green")
      controller.publish(controller.put, Move(2, 1, 1, true))
      controller.publish(controller.put, Move(2, 1, 2, true))
      controller.publish(controller.put, Move(1, 2, 0, true))
      controller.currentPoints should be(2)
      controller.publish(controller.put, Move(2, 1, 3, true))
      controller.publish(controller.put, Move(2, 1, 4, true))
      controller.publish(controller.put, Move(2, 2, 0, true))
      controller.publish(controller.put, Move(2, 2, 1, true))
      controller.publish(controller.put, Move(2, 2, 2, true))
      controller.publish(controller.put, Move(2, 2, 3, true))
      controller.publish(controller.put, Move(2, 2, 4, true))
      controller.publish(controller.put, Move(1, 2, 1, true))
      controller.publish(controller.put, Move(1, 2, 2, true))
      controller.publish(controller.put, Move(1, 2, 3, true))
      controller.publish(controller.put, Move(1, 3, 0, true))
      controller.publish(controller.put, Move(1, 3, 1, true))
      controller.publish(controller.put, Move(1, 3, 2, true))
      controller.publish(controller.put, Move(1, 3, 3, true))
      controller.currentPoints should be(9)
      controller.toString should be(
        "\n\n" +
          "O=======O=======O=======O=======O\n" +
          "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
          "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
          "O=======O=======O=======O=======O\n" +
          "‖   R   ‖   G   ‖   G   ‖   G   ‖\n" +
          "‖   R   ‖   G   ‖   G   ‖   G   ‖\n" +
          "O=======O=======O=======O=======O\n" +
          "‖   G   ‖   G   ‖   G   ‖   G   ‖\n" +
          "‖   G   ‖   G   ‖   G   ‖   G   ‖\n" +
          "O=======O=======O=======O=======O\n\n" +
          "Greens turn\n" +
          "[points: 9]\n\n"
      )

      controller.playerList should be(Vector(Player("Blue", 1, Status.Blue, PlayerType.Human), Player("Red", 2, Status.Red, PlayerType.Human), Player("Green", 9, Status.Green, PlayerType.Human)))
      controller.gameEnded shouldBe true
      controller.winner should be("Player Green wins!")
      controller.stats should be(
        "Player Blue [points: 1]\n" +
          "Player Red [points: 2]\n" +
          "Player Green [points: 9]"
      )

      controller.remove(testObserver)
    }
    "do a cheating move" in {
      val controllerCheat = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      class TestObserver(controller: Controller) extends Observer:
        controllerCheat.add(this)
        var bing = false
        def update(e: Event) = bing = true
      val testObserver = TestObserver(controllerCheat)

      controllerCheat.toString should be(
        "\n\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Blues turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )

      val cheatingMove: List[Option[Move]] = List(
        Some(Move(1, 0, 0, true)),
        Some(Move(1, 1, 0, true)),
        Some(Move(2, 0, 0, true))
      )

      controllerCheat.publishCheat(
        controllerCheat.put,
        PackT(cheatingMove)
      )

      controllerCheat.toString should be(
        "\n\n" +
          "O=======O-------O-------O-------O\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )

      val cheatingMove2: List[Option[Move]] = List(
        Some(Move(2, 0, 1, true)),
        None,
        Some(Move(2, 0, 2, true))
      )

      val result = controllerCheat.publishCheat(
        controllerCheat.put,
        PackT(cheatingMove2)
      )

      controllerCheat.toString should be(
        "\n\n" +
          "O=======O-------O-------O-------O\n" +
          "‖   R   ‖   -   ‖   -   ¦   -   ¦\n" +
          "‖   R   ‖   -   ‖   -   ¦   -   ¦\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Blues turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )

      result should matchPattern { case Failure(_) => }

      val cheatingMove3: List[Option[Move]] = List(
        Some(Move(9, 0, 1, true))
      )

      val result2 = controllerCheat.publishCheat(
        controllerCheat.put,
        PackT(cheatingMove3)
      )

      result2 should matchPattern { case Failure(_) => }

      val result4 = controllerCheat.publishCheat(
        controllerCheat.put,
        PackT(List())
      )

      result shouldBe a[Failure[?]]
    }
    "be able to finish the game with cheat" in {
      val controllerCheat = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      controllerCheat.publishCheat(
        controllerCheat.put,
        PackT(
          List(
            Some(Move(1, 0, 0, true)),
            Some(Move(1, 0, 1, true)),
            Some(Move(1, 0, 2, true)),
            Some(Move(1, 0, 3, true)),
            Some(Move(1, 1, 0, true)),
            Some(Move(1, 1, 1, true)),
            Some(Move(1, 1, 2, true)),
            Some(Move(1, 1, 3, true)),
            Some(Move(1, 2, 0, true)),
            Some(Move(1, 2, 1, true)),
            Some(Move(1, 2, 2, true)),
            Some(Move(1, 2, 3, true)),
            Some(Move(1, 3, 0, true)),
            Some(Move(1, 3, 1, true)),
            Some(Move(1, 3, 2, true)),
            Some(Move(1, 3, 3, true)),
            Some(Move(2, 0, 0, true)),
            Some(Move(2, 0, 1, true)),
            Some(Move(2, 0, 2, true)),
            Some(Move(2, 0, 3, true)),
            Some(Move(2, 0, 4, true)),
            Some(Move(2, 1, 0, true)),
            Some(Move(2, 1, 1, true)),
            Some(Move(2, 1, 2, true)),
            Some(Move(2, 1, 3, true)),
            Some(Move(2, 1, 4, true)),
            Some(Move(2, 2, 0, true)),
            Some(Move(2, 2, 1, true)),
            Some(Move(2, 2, 2, true)),
            Some(Move(2, 2, 3, true)),
            Some(Move(2, 2, 4, true))
          )
        )
      )
      controllerCheat.gameEnded shouldBe true
    }
    "be able to undo and redo" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      val controller2 = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())

      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))

      controller.getRowCell(0, 0) shouldBe true
      controller.getColCell(0, 1) shouldBe true
      controller.getStatusCell(0, 0) should be(Status.Red)

      controller.field.getStatusCell(0, 0) should be(Status.Red)
      controller.field.getColCell(0, 1) shouldBe true

      controller.publish(controller.undo)
      controller.field.getStatusCell(0, 0) should be(Status.Empty)
      controller.field.getColCell(0, 1) shouldBe false

      controller.getColCell(0, 1) shouldBe false

      controller.publish(controller.redo)
      controller.field.getStatusCell(0, 0) should be(Status.Red)
      controller.field.getColCell(0, 1) shouldBe true

      controller.publish(controller.undo)
      controller.publish(controller.undo)
      controller.field.getColCell(0, 0) shouldBe false
      controller.field.getColCell(0, 1) shouldBe false

      controller.publish(controller.redo)
      controller.publish(controller.redo)
      controller.field.getStatusCell(0, 0) should be(Status.Red)
      controller.field.getColCell(0, 0) shouldBe true
      controller.field.getColCell(0, 1) shouldBe true

      controller2.publish(controller2.put, Move(1, 0, 0, true))
      controller2.publish(controller2.put, Move(2, 0, 0, true))

      val undoField = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human).putRow(0, 0, true).nextPlayer
      val redoField = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human).putRow(0, 0, true).putCol(0, 0, true)

      controller2.undo should be(undoField)
      controller2.redo should be(redoField)
    }
    "be able to undo and redo when the game was already finished" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())

      for {
        x <- 0 until controller.field.maxPosY
        y <- 0 until controller.field.maxPosY
      } controller.publish(controller.put, Move(1, x, y, true))

      for {
        x <- 0 until controller.field.maxPosX
        y <- 0 to controller.field.maxPosY
      } controller.publish(controller.put, Move(2, x, y, true))
      
      controller.gameEnded shouldBe true
      controller.publish(controller.undo)
      controller.gameEnded shouldBe false
      controller.publish(controller.redo)
      controller.gameEnded shouldBe true
    }
    "handle nil undo and redo stack" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      val field = controller.field
      controller.undo should be(field)
      controller.redo should be(field)
    }
    "deny wrong input" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))
      /* wrong inputs */
      controller.publish(controller.put, Move(4, 0, 0, true))
      controller.publish(controller.put, Move(1, 9, 0, true))
      controller.publish(controller.put, Move(2, 0, 9, true))
      /* no change */
      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O-------O\n" +
          "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
          "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 1]\n\n" +
          "Your Move <Line><X><Y>: "
      )
    }
    "initial and return its game config data" in {
      val boardSize: BoardSize = BoardSize.Large
      val playerSize: PlayerSize = PlayerSize.Four
      val playerType: PlayerType = PlayerType.Computer
      val computerImpl: ComputerInterface = new ComputerHard
      val controller = new Controller(using new Field(boardSize, Status.Empty, playerSize, playerType), new xmlImpl.FileIO(), computerImpl)
    
      controller.boardSize shouldBe boardSize
      controller.playerSize shouldBe playerSize
      controller.playerType shouldBe playerType
      controller.computerDifficulty shouldBe ComputerDifficulty.Hard
    }
    "return the right computer implementation" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      controller.getComputerImpl(controller.computerDifficulty) shouldBe a[ComputerMedium]
      controller.getComputerImpl(ComputerDifficulty.Easy) shouldBe a[ComputerEasy]
      controller.getComputerImpl(ComputerDifficulty.Medium) shouldBe a[ComputerMedium]
      controller.getComputerImpl(ComputerDifficulty.Hard) shouldBe a[ComputerHard]
    }
    "return the right computer difficulty" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      controller.getComputerDifficulty(controller.computer) shouldBe ComputerDifficulty.Medium
      controller.getComputerDifficulty(ComputerEasy()) shouldBe ComputerDifficulty.Easy
      controller.getComputerDifficulty(ComputerMedium()) shouldBe ComputerDifficulty.Medium
      controller.getComputerDifficulty(ComputerHard()) shouldBe ComputerDifficulty.Hard
    }
    "init a new game" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      controller.initGame(BoardSize.Medium, PlayerSize.Three, PlayerType.Computer, ComputerDifficulty.Easy)

      controller.boardSize shouldBe BoardSize.Medium
      controller.playerSize shouldBe PlayerSize.Three
      controller.playerType shouldBe PlayerType.Computer
      controller.computerDifficulty shouldBe ComputerDifficulty.Easy
    }
    "init a new game and choose computer medium if more than 2 players and computer hard is chosen" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      controller.initGame(BoardSize.Medium, PlayerSize.Three, PlayerType.Computer, ComputerDifficulty.Hard)

      controller.boardSize shouldBe BoardSize.Medium
      controller.playerSize shouldBe PlayerSize.Three
      controller.playerType shouldBe PlayerType.Computer
      controller.computerDifficulty shouldBe ComputerDifficulty.Medium

      controller.initGame(BoardSize.Medium, PlayerSize.Four, PlayerType.Computer, ComputerDifficulty.Hard)
      controller.computerDifficulty shouldBe ComputerDifficulty.Medium

      controller.initGame(BoardSize.Medium, PlayerSize.Four, PlayerType.Computer, ComputerDifficulty.Medium)
      controller.computerDifficulty shouldBe ComputerDifficulty.Medium

      controller.initGame(BoardSize.Medium, PlayerSize.Four, PlayerType.Computer, ComputerDifficulty.Easy)
      controller.computerDifficulty shouldBe ComputerDifficulty.Easy

      controller.initGame(BoardSize.Medium, PlayerSize.Two, PlayerType.Computer, ComputerDifficulty.Hard)
      controller.computerDifficulty shouldBe ComputerDifficulty.Hard
    }
    "restart a game" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))

      controller.getRowCell(0, 0) should be(true)
      controller.getRowCell(1, 0) should be(true)
      controller.getRowCell(0, 1) should be(false)
      controller.getColCell(0, 0) should be(true)
      controller.getColCell(0, 1) should be(true)
      controller.getColCell(1, 0) should be(false)

      controller.getStatusCell(0, 0) shouldBe Status.Red
      controller.currentPlayer should be("Red")
      controller.currentPoints should be(1)

      controller.restart

      controller.getRowCell(0, 0) should be(false)
      controller.getRowCell(1, 0) should be(false)
      controller.getRowCell(0, 1) should be(false)
      controller.getColCell(0, 0) should be(false)
      controller.getColCell(0, 1) should be(false)
      controller.getColCell(1, 0) should be(false)

      controller.getStatusCell(0, 0) shouldBe Status.Empty
      controller.currentPlayer should be("Blue")
      controller.currentPoints should be(0)
    }
    "play against a computer AI" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer), new xmlImpl.FileIO(), new ComputerMedium())
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.getRowCell(0, 0) should be(true)

      val allCells = 
        (for (x <- 0 until controller.field.maxPosY; y <- 0 until controller.field.maxPosY) 
          yield controller.getRowCell(x, y)) ++ 
        (for (x <- 0 until controller.field.maxPosX; y <- 0 to controller.field.maxPosY) 
          yield controller.getColCell(x, y))

      allCells.count(identity) shouldBe 2
    }
    "make a computer move" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer), new xmlImpl.FileIO(), new ComputerMedium())
      val newField = controller.computerMove(controller.field)

      val allCells = 
        (for (x <- 0 until newField.maxPosY; y <- 0 until newField.maxPosY) 
          yield controller.getRowCell(x, y)) ++ 
        (for (x <- 0 until newField.maxPosX; y <- 0 to newField.maxPosY) 
          yield controller.getColCell(x, y))

      allCells.count(identity) shouldBe 2
    }
    "handle bad computer move" in {
      val mockComputerImpl = mock(classOf[ComputerHard])
      val initialField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer)
      val controller = new Controller(using initialField, new xmlImpl.FileIO(), mockComputerImpl)
      controller.publish(controller.put, Move(9, 9, 9, true)) shouldBe a[Failure[?]]
      
      when(mockComputerImpl.calculateMove(initialField)).thenReturn(Some(Move(9, 9, 9, true)))
      
      val newField = controller.computerMove(controller.field)

      val allCells = 
        (for (x <- 0 until newField.maxPosY; y <- 0 until newField.maxPosY) 
          yield controller.getRowCell(x, y)) ++ 
        (for (x <- 0 until newField.maxPosX; y <- 0 to newField.maxPosY) 
          yield controller.getColCell(x, y))

      allCells.count(identity) shouldBe 0
      newField shouldBe initialField
    }
    "handle None computer move" in {
      val mockComputerImpl = mock(classOf[ComputerHard])
      val initialField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer)
      val controller = new Controller(using initialField, new xmlImpl.FileIO(), mockComputerImpl)
      
      when(mockComputerImpl.calculateMove(initialField)).thenReturn(None)
      
      val newField = controller.computerMove(controller.field)

      val allCells = 
        (for (x <- 0 until newField.maxPosY; y <- 0 until newField.maxPosY) 
          yield controller.getRowCell(x, y)) ++ 
        (for (x <- 0 until newField.maxPosX; y <- 0 to newField.maxPosY) 
          yield controller.getColCell(x, y))

      allCells.count(identity) shouldBe 0
      newField shouldBe initialField
    }
  }
}
