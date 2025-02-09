package de.htwg.se.dotsandboxes

import controller.controllerComponent.ControllerInterface
import controller.controllerComponent.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.fileIoComponent._
import model.matrixComponent.matrixImpl.Status

object Default:
  given FieldInterface = new Field(5, 4, Status.Empty, 2)
  given FileIOInterface = xmlImpl.FileIO()
  given ControllerInterface = Controller()
