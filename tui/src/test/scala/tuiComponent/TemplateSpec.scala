package tui
package tuiComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.dotsandboxes.util.Move
import scala.util.Try
import de.htwg.se.dotsandboxes.util.Event
import de.htwg.se.dotsandboxes.controller.controllerComponent.controllerImpl.Controller
import de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl.Field
import de.htwg.se.dotsandboxes.util.{BoardSize, PlayerSize, PlayerType}
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.model.computerComponent.computerMediumImpl.ComputerMedium
import de.htwg.se.dotsandboxes.model.fileIoComponent._

class TemplateSpec extends AnyWordSpec {
  "Template" should {
    val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Human), new xmlImpl.FileIO(), new ComputerMedium())
    val template = new Template(controller) {
      override def gameLoop: Unit = ???
      override def analyzeInput(input: String): Option[Move] = ???
      override def finalStats: String = ???
      override def checkSyntax(vec: Char, x: Char, y: Char): Try[(Int, Int, Int)] = ???
      override def syntaxErr: String = ???
      override def update(event: Event): Unit = ???
    }
    "return the right string on welcoming" in {
      template.welcoming should be(
        "\n" +
        "-----------------------------------\n" +
        "| Welcome to Dots And Boxes TUI ! |\n" +
        "-----------------------------------\n"
      )
    }
    "return the right string on help" in {
      template.help should be(
        "--Note\n\n" +
        "A move consists of:\n\n" +
        "<Line> index: (1) for horizontally, (2) for vertically\n" +
        "<X> coordinate: starting at (0)\n" +
        "<Y> coordinate: starting at (0)\n" +
        "e.g: 132\n\n" +
        "You can type (q) to quit, (z) to undo (y) to redo, (r) to restart,\n" +
        "(h) for help, (s) to save the current game state or (l) to load it.\n\n" +
        "If you want to start a new game with different settings you can type 'NEW: '\n" +
        "followed by this space separated options:\n\n" +
        "<Board size>:          (1) for 4x3, (2) for 5x4, (3) for 8x6\n" +
        "<Player size>:         (2), (3), (4)\n" +
        "<Player type>:         (1) for humans, (2) for computers\n" +
        "<Computer difficulty>: (1) for easy, (2) for medium, (3) for hard\n" +
        "e.g: NEW: 2 3 2 1\n"
      )
    }
  }
}