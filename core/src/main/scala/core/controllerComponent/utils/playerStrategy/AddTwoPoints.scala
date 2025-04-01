package core.controllerComponent.utils.playerStrategy

import common.model.fieldService.FieldInterface
import core.api.service.ModelRequestHttp

object AddTwoPoints extends PlayerState:
  override def handle(field: FieldInterface): String = ModelRequestHttp.addPlayerPoints(2, field)
