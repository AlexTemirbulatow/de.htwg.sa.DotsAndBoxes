object ModelService:
  @main def startModelServer: Unit = api.server.ModelHttpServer.run
