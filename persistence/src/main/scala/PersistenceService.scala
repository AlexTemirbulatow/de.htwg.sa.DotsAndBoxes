object PersistenceService:
  @main def startPersistenceServer: Unit = api.server.PersistenceServer.run
