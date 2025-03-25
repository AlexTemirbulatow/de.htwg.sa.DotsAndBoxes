package controllerComponent.controllerImpl.playerStrategy

import api.utils.ModelRequestHttp
import fieldComponent.FieldInterface

object AddOnePoint extends PlayerState:
  override def handle(field: FieldInterface): String = ModelRequestHttp.addPlayerPoints(1, field)
