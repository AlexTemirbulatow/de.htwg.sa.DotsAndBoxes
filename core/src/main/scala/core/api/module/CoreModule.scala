package core.api.module

import common.model.fieldService.FieldInterface
import core.controllerComponent.ControllerInterface
import core.controllerComponent.controllerImpl.Controller
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field

object CoreModule:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  given FileFormat = FileFormat.JSON
  given ComputerDifficulty = ComputerDifficulty.Medium
  given ControllerInterface = Controller()
