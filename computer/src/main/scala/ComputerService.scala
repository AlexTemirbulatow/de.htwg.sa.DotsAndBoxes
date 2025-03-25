object ComputerService:
  @main def startComputerServer: Unit = api.server.ComputerHttpServer.run
