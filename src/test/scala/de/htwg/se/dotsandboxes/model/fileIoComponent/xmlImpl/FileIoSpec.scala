package de.htwg.se.dotsandboxes
package model
package fileIoComponent.xmlImpl

import controller.controllerComponent.controllerImpl.Controller
import de.htwg.se.dotsandboxes.model.fileIoComponent.FileIOInterface
import fieldComponent.fieldImpl.Field
import fileIoComponent.xmlImpl.FileIO
import matrixComponent.matrixImpl.Status
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import util.Move
import de.htwg.se.dotsandboxes.util.{BoardSize, PlayerSize, PlayerType}
import de.htwg.se.dotsandboxes.model.computerComponent.computerMediumImpl.ComputerMedium

class FileIoSpec extends AnyWordSpec {
  "A game state" when {
    "saved to xml" should {
      "be equal when loaded" in {
        val field: Field = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val fileIO = new FileIO()
        fileIO.save(field)
        fileIO.load should be(field)
      }
      "return the correct game state" in {
        val controller = Controller(using new Field(BoardSize.Medium, Status.Empty, PlayerSize.Four, PlayerType.Human), new FileIO(), new ComputerMedium())
        controller.publish(controller.put, Move(1, 0, 0, true))
        controller.publish(controller.put, Move(2, 0, 0, true))
        controller.publish(controller.put, Move(2, 0, 1, true))
        controller.publish(controller.put, Move(1, 1, 0, true))

        controller.publish(controller.put, Move(1, 0, 1, true))
        controller.publish(controller.put, Move(2, 0, 2, true))
        controller.publish(controller.put, Move(1, 1, 1, true))

        controller.publish(controller.put, Move(1, 0, 2, true))
        controller.publish(controller.put, Move(2, 0, 3, true))
        controller.publish(controller.put, Move(2, 1, 0, true))
        controller.publish(controller.put, Move(1, 1, 2, true))

        controller.publish(controller.put, Move(1, 0, 3, true))
        controller.publish(controller.put, Move(2, 0, 4, true))
        controller.publish(controller.put, Move(1, 1, 3, true))

        controller.save should be(controller.field)
        controller.load should be(controller.field)
      }
      "return a finished game state" in {
        val mockFileIO = mock(classOf[FileIOInterface])
        val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), mockFileIO, new ComputerMedium())
        for {
          x <- 0 until controller.field.maxPosY
          y <- 0 until controller.field.maxPosY
        } controller.publish(controller.put, Move(1, x, y, true))

        for {
          x <- 0 until controller.field.maxPosX
          y <- 0 to controller.field.maxPosY
        } controller.publish(controller.put, Move(2, x, y, true))

        controller.gameEnded shouldBe true
        controller.save should be(controller.field)

        when(mockFileIO.load).thenReturn(controller.field)
        controller.load should be(controller.field)

        controller.gameEnded shouldBe true
      }
      "return Left if something went wrong" in {
        val fileIO = new FileIO()
        fileIO.save(null) should matchPattern { case Left(_) => }
      }
    }
  }
}
