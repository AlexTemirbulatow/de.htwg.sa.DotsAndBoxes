package controllerComponent.controllerImpl.playerStrategy

import common.model.fieldService.FieldInterface

trait PlayerState { def handle(field: FieldInterface): String }
