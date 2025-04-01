object ComputerService:
  @main def startComputerServer: Unit = computer.api.server.ComputerHttpServer.run
