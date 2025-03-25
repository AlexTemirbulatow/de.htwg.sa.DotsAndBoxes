package api.module

import controllerComponent.ControllerInterface
import controllerComponent.controllerImpl.Controller
import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import fileIoComponent.FileIOInterface
import fileIoComponent.jsonImpl.FileIO
import computerComponent.ComputerInterface
import computerComponent.computerMediumImpl.ComputerMedium
import de.github.dotsandboxes.lib.{PlayerType, PlayerSize, BoardSize, Status}

object CoreModule:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  given FileIOInterface = FileIO()
  given ComputerInterface = ComputerMedium()
  given ControllerInterface = Controller()
