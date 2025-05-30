package core.controllerComponent.utils.command

import common.model.fieldService.FieldInterface
import core.api.service.ModelRequestHttp
import de.github.dotsandboxes.lib.Move

class PutCommand(move: Move, var field: FieldInterface) extends Command:
  override def doStep(field: FieldInterface): String =
    move.vec match
      case 1 => ModelRequestHttp.putRow(move.x, move.y, move.value, field)
      case 2 => ModelRequestHttp.putCol(move.x, move.y, move.value, field)
  override def undoStep(field: FieldInterface): FieldInterface =
    val temp = this.field
    this.field = field
    temp
  override def redoStep(field: FieldInterface): FieldInterface =
    val temp = this.field
    this.field = field
    temp
