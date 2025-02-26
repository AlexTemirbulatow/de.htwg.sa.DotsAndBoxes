package de.htwg.se.dotsandboxes

import controller.controllerComponent.ControllerInterface
import controller.controllerComponent.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.fileIoComponent.{FileIOInterface, jsonImpl, xmlImpl}
import model.computerComponent.{ComputerInterface, computerEasyImpl, computerMediumImpl, computerHardImpl}
import model.matrixComponent.matrixImpl.Status
import util.{PlayerType, PlayerSize, BoardSize}

object Default:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  given FileIOInterface = jsonImpl.FileIO()
  given ComputerInterface = computerMediumImpl.ComputerMedium()
  given ControllerInterface = Controller()
