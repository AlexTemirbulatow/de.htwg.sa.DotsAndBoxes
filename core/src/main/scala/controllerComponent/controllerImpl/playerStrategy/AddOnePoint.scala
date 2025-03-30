package controllerComponent.controllerImpl.playerStrategy

import api.service.ModelRequestHttp
import common.model.fieldService.FieldInterface

object AddOnePoint extends PlayerState:
  override def handle(field: FieldInterface): String = ModelRequestHttp.addPlayerPoints(1, field)
