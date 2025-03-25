package controllerComponent.controllerImpl.playerStrategy

import api.service.ModelRequestHttp
import fieldComponent.FieldInterface

object AddTwoPoints extends PlayerState:
  override def handle(field: FieldInterface): String = ModelRequestHttp.addPlayerPoints(2, field)
