object TUIService:
  @main def startTuiServer: Unit = api.server.TUIHttpServer.run
