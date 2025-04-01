object GUIService:
  @main def startGuiServer: Unit = gui.api.server.GUIHttpServer.run
