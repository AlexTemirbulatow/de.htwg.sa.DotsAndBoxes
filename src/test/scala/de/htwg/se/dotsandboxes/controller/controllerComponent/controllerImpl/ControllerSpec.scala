package de.htwg.se.dotsandboxes
package controller.controllerComponent.controllerImpl

import de.htwg.se.dotsandboxes.Default.given_FieldInterface
import de.htwg.se.dotsandboxes.Default.given_FileIOInterface
import de.htwg.se.dotsandboxes.controller.controllerComponent.ControllerInterface
import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface
import de.htwg.se.dotsandboxes.model.fileIoComponent._
import de.htwg.se.dotsandboxes.util.PackT
import model.fieldComponent.fieldImpl.Field
import model.matrixComponent.matrixImpl.{Player, Status}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.util.Failure
import util.Move
import util.{Event, Observer}

class ControllerSpec extends AnyWordSpec {
  val controller = Controller(using new Field(3, 3, Status.Empty, 3), new xmlImpl.FileIO())
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
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
          "Blues turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )

      testObserver.bing shouldBe false
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.gameEnded shouldBe false
      testObserver.bing shouldBe true

      controller.playerList should be(Vector(Player("Blue", 0, Status.Blue), Player("Red", 0, Status.Red), Player("Green", 0, Status.Green)))
      controller.rowSize() should be(4)
      controller.colSize() should be(4)

      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(1, 1, 1, true))
      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O=======O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
          "Greens turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(2, 0, 1, true))
      controller.toString should be(
        "\n\n" +
          "O=======O-------O-------O\n" +
          "¦   -   ‖   -   ¦   -   ¦\n" +
          "¦   -   ‖   -   ¦   -   ¦\n" +
          "O-------O=======O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
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
          "O=======O-------O-------O\n" +
          "‖   R   ‖   -   ¦   -   ¦\n" +
          "‖   R   ‖   -   ¦   -   ¦\n" +
          "O=======O=======O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 1]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(1, 0, 1, true))
      controller.publish(controller.put, Move(1, 0, 2, true))
      controller.publish(controller.put, Move(1, 1, 2, true))
      controller.publish(controller.put, Move(2, 0, 3, true))
      controller.publish(controller.put, Move(2, 0, 2, true))
      controller.toString should be(
        "\n\n" +
          "O=======O=======O=======O\n" +
          "‖   R   ‖   G   ‖   G   ‖\n" +
          "‖   R   ‖   G   ‖   G   ‖\n" +
          "O=======O=======O=======O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
          "Greens turn\n" +
          "[points: 2]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.currentPlayer should be("Green")
      controller.publish(controller.put, Move(2, 1, 0, true))
      controller.publish(controller.put, Move(2, 1, 1, true))
      controller.publish(controller.put, Move(2, 1, 2, true))
      controller.publish(controller.put, Move(1, 2, 0, true))
      controller.currentPoints should be(3)
      controller.publish(controller.put, Move(2, 1, 3, true))
      controller.publish(controller.put, Move(2, 2, 0, true))
      controller.publish(controller.put, Move(2, 2, 1, true))
      controller.publish(controller.put, Move(2, 2, 2, true))
      controller.publish(controller.put, Move(2, 2, 3, true))
      controller.publish(controller.put, Move(1, 2, 1, true))
      controller.publish(controller.put, Move(1, 2, 2, true))
      controller.publish(controller.put, Move(1, 3, 0, true))
      controller.publish(controller.put, Move(1, 3, 1, true))
      controller.publish(controller.put, Move(1, 3, 2, true))
      controller.currentPoints should be(6)
      controller.toString should be(
        "\n\n" +
          "O=======O=======O=======O\n" +
          "‖   R   ‖   G   ‖   G   ‖\n" +
          "‖   R   ‖   G   ‖   G   ‖\n" +
          "O=======O=======O=======O\n" +
          "‖   G   ‖   R   ‖   R   ‖\n" +
          "‖   G   ‖   R   ‖   R   ‖\n" +
          "O=======O=======O=======O\n" +
          "‖   R   ‖   R   ‖   R   ‖\n" +
          "‖   R   ‖   R   ‖   R   ‖\n" +
          "O=======O=======O=======O\n\n" +
          "Reds turn\n" +
          "[points: 6]\n\n"
      )

      controller.playerList should be(Vector(Player("Blue", 0, Status.Blue), Player("Red", 6, Status.Red), Player("Green", 3, Status.Green)))
      controller.gameEnded shouldBe true
      controller.winner should be("Player Red wins!")
      controller.stats should be(
        "Player Blue [points: 0]\n" +
        "Player Red [points: 6]\n" +
        "Player Green [points: 3]"
      )

      controller.remove(testObserver)
    }
    "do a cheating move" in {
      val controllerCheat = Controller(using new Field(3, 3, Status.Empty, 2), new xmlImpl.FileIO())
      class TestObserver(controller: Controller) extends Observer:
        controllerCheat.add(this)
        var bing = false
        def update(e: Event) = bing = true
      val testObserver = TestObserver(controllerCheat)

      controllerCheat.toString should be(
        "\n\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
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
          "O=======O-------O-------O\n" +
          "‖   -   ¦   -   ¦   -   ¦\n" +
          "‖   -   ¦   -   ¦   -   ¦\n" +
          "O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
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
          "O=======O-------O-------O\n" +
          "‖   R   ‖   -   ‖   -   ¦\n" +
          "‖   R   ‖   -   ‖   -   ¦\n" +
          "O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
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
      val controllerCheat = Controller(using new Field(1, 1, Status.Empty, 2), new xmlImpl.FileIO())
      controllerCheat.publishCheat(
        controllerCheat.put,
        PackT(List(
          Some(Move(1, 0, 0, true)),
          Some(Move(1, 1, 0, true)),
          Some(Move(2, 0, 0, true)),
          Some(Move(2, 0, 1, true))
        ))
      )
      controllerCheat.gameEnded shouldBe true
    }
    "be able to undo and redo" in {
      val controller = Controller(using new Field(3, 3, Status.Empty, 2), new xmlImpl.FileIO())
      val controller2 = Controller(using new Field(3, 3, Status.Empty, 2), new xmlImpl.FileIO())

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

      val undoField = new Field(3, 3, Status.Empty, 2).putRow(0, 0, true).nextPlayer
      val redoField = new Field(3, 3, Status.Empty, 2).putRow(0, 0, true).putCol(0, 0, true)

      controller2.undo should be(undoField)
      controller2.redo should be(redoField)
    }
    "be able to undo and redo when the game was already finished" in {
      val controller = Controller(using new Field(1, 1, Status.Empty, 2), new xmlImpl.FileIO())

      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))

      controller.gameEnded shouldBe true
      controller.publish(controller.undo)
      controller.gameEnded shouldBe false
      controller.publish(controller.redo)
      controller.gameEnded shouldBe true
    }
    "handle nil undo and redo stack" in {
      val controller = new Controller(using new Field(3, 3, Status.Empty, 2), new xmlImpl.FileIO())
      val field = controller.field
      controller.undo should be(field)
      controller.redo should be(field)
    }
    "deny wrong input" in {
      val controller = new Controller(using new Field(3, 3, Status.Empty, 2), new xmlImpl.FileIO())
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
          "O=======O-------O-------O\n" +
          "‖   R   ‖   -   ¦   -   ¦\n" +
          "‖   R   ‖   -   ¦   -   ¦\n" +
          "O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 1]\n\n" +
          "Your Move <Line><X><Y>: "
      )
    }
  }
}
