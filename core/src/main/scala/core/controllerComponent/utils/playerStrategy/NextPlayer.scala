package core.controllerComponent.utils.playerStrategy

import common.model.fieldService.FieldInterface
import core.api.service.ModelRequestHttp

object NextPlayer extends PlayerState:
  override def handle(field: FieldInterface): String = ModelRequestHttp.nextPlayer(field)
