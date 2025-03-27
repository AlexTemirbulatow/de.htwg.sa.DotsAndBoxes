package api.module

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, Status}
import fieldComponent.fieldImpl.Field

object FieldModule:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
