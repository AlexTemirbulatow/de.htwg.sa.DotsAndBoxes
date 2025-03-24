package controllerComponent.controllerImpl.playerStrategy

import fieldComponent.FieldInterface

trait PlayerState { def handle(field: FieldInterface): String }
