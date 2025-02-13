package de.htwg.se.dotsandboxes.aview

import de.htwg.se.dotsandboxes.controller.controllerComponent.ControllerInterface
import de.htwg.se.dotsandboxes.controller.controllerComponent.controllerImpl.Controller
import de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl.Field
import de.htwg.se.dotsandboxes.model.fileIoComponent._
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.Event
import de.htwg.se.dotsandboxes.util.Move
import de.htwg.se.dotsandboxes.util.PackT
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.compiletime.uninitialized
import scala.util.{Failure, Success}

class TUISpec extends AnyWordSpec with BeforeAndAfterEach {
  var mockController: ControllerInterface = uninitialized
  var tui: TUI = uninitialized

  override def beforeEach(): Unit = {
    mockController = mock(classOf[ControllerInterface])
    tui = new TUI(using mockController)
  }

  "TUI" when {
    "given an input" should {
      "properly analyze keywords" in {
        //tui.analyzeInput("q") shouldBe None
        tui.analyzeInput("z") shouldBe None
        tui.analyzeInput("y") shouldBe None
        tui.analyzeInput("s") shouldBe None
        tui.analyzeInput("l") shouldBe None
      }
      "properly analyze a correct move" in {
        val expectedMove: Move = Move(1, 0, 0, true)
        tui.analyzeInput("100") shouldBe Some(expectedMove)
      }
      "return None on invalid move with length 3" in {
        tui.analyzeInput("abc") shouldBe None
        verify(mockController, never()).publish(any(), any())
      }
      "return None on invalid move" in {
        tui.analyzeInput("xyz123") shouldBe None
        verify(mockController, never()).publish(any(), any())
      }
      "allow a cheat keyword with following cheat moves" in {
        tui.analyzeInput("CHEAT: 100 110 200") shouldBe None
        val packCaptor: ArgumentCaptor[PackT[Option[Move]]] =
          ArgumentCaptor.forClass(classOf[PackT[?]]).asInstanceOf[ArgumentCaptor[PackT[Option[Move]]]]

        verify(mockController).publishCheat(any(), packCaptor.capture())
        packCaptor.getValue shouldBe PackT(
          List(
            Some(Move(1, 0, 0, true)),
            Some(Move(1, 1, 0, true)),
            Some(Move(2, 0, 0, true))
          )
        )
      }
      "allow cheat moves and have Nones" in {
        tui.analyzeInput("CHEAT: 122 abc 222") shouldBe None
        val packCaptor: ArgumentCaptor[PackT[Option[Move]]] =
          ArgumentCaptor.forClass(classOf[PackT[?]]).asInstanceOf[ArgumentCaptor[PackT[Option[Move]]]]

        verify(mockController).publishCheat(any(), packCaptor.capture())
        packCaptor.getValue shouldBe PackT(
          List(
            Some(Move(1, 2, 2, true)),
            None,
            Some(Move(2, 2, 2, true))
          )
        )
      }
    }
    "checking the syntax of a move" should {
      "return success on proper input" in {
        tui.checkSyntax('1', '2', '3') shouldBe Success((1, 2, 3))
      }
      "return failure on invalid input" in {
        tui.checkSyntax('a', 'b', 'c') shouldBe a[Failure[?]]
      }
    }
    "printing a string" should {
      "return a correct final stats string representation" in {
        when(mockController.winner).thenReturn("Player Blue wins!")
        when(mockController.stats).thenReturn("Player Blue [points: 17]\nPlayer Red [points: 3]")
        tui.finalStats should be(
          "\n" +
            "Player Blue wins!\n" +
            "_________________________\n\n" +
            "Player Blue [points: 17]\nPlayer Red [points: 3]" +
            "\n"
        )
      }
      "return a correct syntax error string representation" in {
        tui.syntaxErr should be("\nIncorrect syntax. Try again: ")
      }
    }
  }
}
