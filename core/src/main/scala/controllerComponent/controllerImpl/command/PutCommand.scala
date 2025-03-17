package controllerComponent.controllerImpl.command

import fieldComponent.FieldInterface
import de.github.dotsandboxes.lib.Move

class PutCommand(move: Move, var field: FieldInterface) extends Command:
  override def doStep(field: FieldInterface): FieldInterface =
    move.vec match
      case 1 => field.putRow(move.x, move.y, move.value)
      case 2 => field.putCol(move.x, move.y, move.value)
  override def undoStep(field: FieldInterface): FieldInterface =
    val temp = this.field
    this.field = field
    temp
  override def redoStep(field: FieldInterface): FieldInterface =
    val temp = this.field
    this.field = field
    temp
