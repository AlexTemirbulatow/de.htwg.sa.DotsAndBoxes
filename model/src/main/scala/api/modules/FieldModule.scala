package api.modules

import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import lib.{BoardSize, PlayerSize, PlayerType, Status}

object FieldModule:
  given FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
