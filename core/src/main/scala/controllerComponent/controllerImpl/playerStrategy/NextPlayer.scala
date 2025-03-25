package controllerComponent.controllerImpl.playerStrategy

import api.utils.ModelRequestHttp
import fieldComponent.FieldInterface

object NextPlayer extends PlayerState:
  override def handle(field: FieldInterface): String = ModelRequestHttp.nextPlayer(field)
