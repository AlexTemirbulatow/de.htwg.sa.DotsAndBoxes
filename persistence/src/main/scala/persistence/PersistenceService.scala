object PersistenceService:
  @main def startPersistenceServer: Unit = persistence.api.server.PersistenceServer.run
