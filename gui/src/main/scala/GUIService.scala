import api.modules.CoreModule.given

object GUIService:
  @main def startGUI: Unit = guiComponent.GUI()
