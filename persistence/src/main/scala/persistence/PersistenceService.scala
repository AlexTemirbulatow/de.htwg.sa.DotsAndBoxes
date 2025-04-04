object PersistenceService:
  @main def startPersistenceServer: Unit = persistence.api.server.PersistenceHttpServer.run
