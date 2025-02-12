package de.htwg.se.dotsandboxes
package model
package fileIoComponent.xmlImpl

import controller.controllerComponent.controllerImpl.Controller
import fieldComponent.fieldImpl.{Field}
import util.Move
import fileIoComponent.xmlImpl.FileIO
import matrixComponent.matrixImpl.Status
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class FileIoSpec extends AnyWordSpec {
  "A game state" when {
    "saved to xml" should {
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
    }
  }
}
