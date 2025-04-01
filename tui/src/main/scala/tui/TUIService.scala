object TUIService:
  @main def startTuiServer: Unit = tui.api.server.TUIHttpServer.run
