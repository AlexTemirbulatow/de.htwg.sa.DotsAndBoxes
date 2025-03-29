package api.module

import common.model.fieldService.FieldInterface
import controllerComponent.ControllerInterface
import controllerComponent.controllerImpl.Controller
import de.github.dotsandboxes.lib._
import fieldComponent.fieldImpl.Field

object CoreModule:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  given FileFormat = FileFormat.JSON
  given ComputerDifficulty = ComputerDifficulty.Medium
  given ControllerInterface = Controller()
