object GUIService:
  @main def startGuiServer: Unit = api.server.GUIHttpServer.run
