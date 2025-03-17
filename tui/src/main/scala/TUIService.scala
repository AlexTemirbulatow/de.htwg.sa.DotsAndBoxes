object TUIService:
  @main def startTUI: Unit = api.server.TUIHttpServer.run
