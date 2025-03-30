package controllerComponent.controllerImpl.playerStrategy

import api.service.ModelRequestHttp
import common.model.fieldService.FieldInterface

object NextPlayer extends PlayerState:
  override def handle(field: FieldInterface): String = ModelRequestHttp.nextPlayer(field)
