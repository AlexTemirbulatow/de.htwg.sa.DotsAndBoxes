package de.htwg.se.dotsandboxes
package model
package fileIoComponent.jsonImpl

import controller.controllerComponent.controllerImpl.Controller
import de.htwg.se.dotsandboxes.model.fileIoComponent.FileIOInterface
import fieldComponent.fieldImpl.Field
import fileIoComponent.jsonImpl.FileIO
import matrixComponent.matrixImpl.Status
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import util.Move

class FileIoSpec extends AnyWordSpec {
  "A game state" when {
    "saved to json" should {
      "be equal when loaded" in {
        val field: Field = new Field(5, 4, Status.Empty, 2)
        val fileIO = new FileIO()
        fileIO.save(field)
        fileIO.load should be(field)
      }
      "return the correct game state" in {
        val controller = Controller(using new Field(5, 4, Status.Empty, 4), new FileIO())
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
        val controller = Controller(using new Field(1, 1, Status.Empty, 2), mockFileIO)
        controller.publish(controller.put, Move(1, 0, 0, true))
        controller.publish(controller.put, Move(1, 1, 0, true))
        controller.publish(controller.put, Move(2, 0, 0, true))
        controller.publish(controller.put, Move(2, 0, 1, true))

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
