package core

import controller.controllerComponent.ControllerInterface
import controller.controllerComponent.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.fileIoComponent._
import model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.PlayerType
import model.computerComponent._
import de.htwg.se.dotsandboxes.util.PlayerSize
import de.htwg.se.dotsandboxes.util.BoardSize

object Default:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  given FileIOInterface = jsonImpl.FileIO()
  given ComputerInterface = computerMediumImpl.ComputerMedium()
  given ControllerInterface = Controller()
