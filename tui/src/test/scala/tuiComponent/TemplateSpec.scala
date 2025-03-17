package tuiComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import org.mockito.Mockito._
import scala.util.Try

import controllerComponent.ControllerInterface
import controllerComponent.controllerImpl.observer.Event
import de.github.dotsandboxes.lib.Move

class TemplateSpec extends AnyWordSpec {
  "Template" should {
    val mockController: ControllerInterface = mock(classOf[ControllerInterface])
    val template = new Template(mockController) {
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
        "---------------------------------\n" +
        "| Welcome to Dots And Boxes TUI |\n" +
        "---------------------------------\n"
      )
    }
    "return the right string on help" in {
      template.help should be(
        "--Note\n\n" +
        "A move consists of:\n\n" +
        "<Line> index: (1) for horizontally, (2) for vertically\n" +
        "<X> coordinate: starting at (0)\n" +
        "<Y> coordinate: starting at (0)\n" +
        "e.g., 132\n\n" +
        "You can type (q) to quit, (z) to undo (y) to redo, (r) to restart,\n" +
        "(h) for help, (s) to save the current game state or (l) to load it.\n\n" +
        "If you want to start a new game with different settings you can type 'NEW: '\n" +
        "followed by this space separated options:\n\n" +
        "<Board size>:          (1) for 4x3, (2) for 5x4, (3) for 8x6\n" +
        "<Player size>:         (2), (3), (4)\n" +
        "<Player type>:         (1) for humans, (2) for computers\n" +
        "<Computer difficulty>: (1) for easy, (2) for medium, (3) for hard\n" +
        "e.g., NEW: 2 3 2 1\n"
      )
    }
  }
}
