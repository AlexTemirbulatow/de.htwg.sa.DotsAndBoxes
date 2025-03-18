object CoreService:
  @main def startCoreServer: Unit = api.server.CoreHttpServer.run
