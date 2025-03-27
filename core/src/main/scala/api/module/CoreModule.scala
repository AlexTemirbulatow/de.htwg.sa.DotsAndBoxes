package api.module

import common.model.fieldService.FieldInterface
import controllerComponent.ControllerInterface
import controllerComponent.controllerImpl.Controller
import de.github.dotsandboxes.lib.{BoardSize, Status, PlayerSize, PlayerType, ComputerDifficulty}
import fieldComponent.fieldImpl.Field
import fileIoComponent.FileIOInterface
import fileIoComponent.{jsonImpl, xmlImpl}

object CoreModule:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  given FileIOInterface = jsonImpl.FileIO()
  given ComputerDifficulty = ComputerDifficulty.Medium
  given ControllerInterface = Controller()
