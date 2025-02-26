package de.htwg.se.dotsandboxes
package aview

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.compiletime.uninitialized
import scala.util.{Failure, Success}
import java.io.{ByteArrayOutputStream, PrintStream}

import controller.controllerComponent.ControllerInterface
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import util.{Event, Move, PackT, PlayerSize, PlayerType, BoardSize, ComputerDifficulty}

class TUISpec extends AnyWordSpec with BeforeAndAfterEach {
  var mockController: ControllerInterface = uninitialized
  var tui: TUI = uninitialized

  override def beforeEach(): Unit = {
    mockController = mock(classOf[ControllerInterface])
    tui = new TUI(using mockController)
  }

  "TUI" when {
    "having an update" should {
      "print the field when Event.Moev is received" in {
        when(mockController.toString).thenReturn("Mocked Field Output")
        
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outputStream)) {
          tui.update(Event.Move)
        }
        
        val output = outputStream.toString.trim
        output should be("Mocked Field Output")
      }
      "print finalStats when Event.End is received" in {
        val outputStream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outputStream)) {
          tui.update(Event.End)
        }
        verify(mockController).winner
        verify(mockController).stats
        val output = outputStream.toString.trim
        output should be(tui.finalStats.toString.trim)
      }
    }
    "given an input" should {
      "properly analyze keywords" in {
        //tui.analyzeInput("q") shouldBe None
        tui.analyzeInput("z") shouldBe None
        tui.analyzeInput("y") shouldBe None
        tui.analyzeInput("s") shouldBe None
        tui.analyzeInput("l") shouldBe None
        tui.analyzeInput("r") shouldBe None
        tui.analyzeInput("h") shouldBe None
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
      "start a new game with different settings" in {
        tui.analyzeInput("NEW: 2 3 2 1") shouldBe None

        val boardSizeCaptor: ArgumentCaptor[BoardSize] =
          ArgumentCaptor.forClass(classOf[BoardSize])

        val playerSizeCaptor: ArgumentCaptor[PlayerSize] =
          ArgumentCaptor.forClass(classOf[PlayerSize])

        val playerTypeCaptor: ArgumentCaptor[PlayerType] =
          ArgumentCaptor.forClass(classOf[PlayerType])

        val computerDifficulty: ArgumentCaptor[ComputerDifficulty] =
          ArgumentCaptor.forClass(classOf[ComputerDifficulty])

        verify(mockController).initGame(
          boardSizeCaptor.capture(),
          playerSizeCaptor.capture(),
          playerTypeCaptor.capture(),
          computerDifficulty.capture()
        )

        boardSizeCaptor.getValue shouldBe BoardSize.Medium
        playerSizeCaptor.getValue shouldBe PlayerSize.Three
        playerTypeCaptor.getValue shouldBe PlayerType.Computer
        computerDifficulty.getValue shouldBe ComputerDifficulty.Easy
      }
      "allow a cheat keyword with following cheat moves" in {
        tui.analyzeInput("CHEAT: 100 110 200") shouldBe None

        val commandCaptor: ArgumentCaptor[Move => FieldInterface] =
          ArgumentCaptor.forClass(classOf[Move => FieldInterface])

        val packCaptor: ArgumentCaptor[PackT[Option[Move]]] =
          ArgumentCaptor.forClass(classOf[PackT[?]]).asInstanceOf[ArgumentCaptor[PackT[Option[Move]]]]

        verify(mockController).publishCheat(commandCaptor.capture(), packCaptor.capture())

        val move = Move(1, 0, 0, true)
        val capturedFunc = commandCaptor.getValue
        capturedFunc(move) shouldBe mockController.put(move)

        packCaptor.getValue shouldBe PackT(
          List(
            Some(Move(1, 0, 0, true)),
            Some(Move(1, 1, 0, true)),
            Some(Move(2, 0, 0, true))
          )
        )
      }
      "allow cheat moves have Nones" in {
        tui.analyzeInput("CHEAT: 122 abc 222") shouldBe None

        val commandCaptor: ArgumentCaptor[Move => FieldInterface] =
          ArgumentCaptor.forClass(classOf[Move => FieldInterface])

        val packCaptor: ArgumentCaptor[PackT[Option[Move]]] =
          ArgumentCaptor.forClass(classOf[PackT[?]]).asInstanceOf[ArgumentCaptor[PackT[Option[Move]]]]

        verify(mockController).publishCheat(commandCaptor.capture(), packCaptor.capture())

        val move = Move(1, 2, 2, true)
        val capturedFunc = commandCaptor.getValue
        capturedFunc(move) shouldBe mockController.put(move)

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
