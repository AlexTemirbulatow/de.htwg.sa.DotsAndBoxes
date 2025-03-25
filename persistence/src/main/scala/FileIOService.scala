object FileIOService:
  @main def startFileIOServer: Unit = api.server.FileIOHttpServer.run
