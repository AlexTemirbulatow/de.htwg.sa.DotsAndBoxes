package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.GUIRoutes
import api.service.CoreRequestHttp
import common.config.ServiceConfig.{GUI_HOST, GUI_OBSERVER_URL, GUI_PORT}
import guiComponent.GUI
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}

object GUIHttpServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    CoreRequestHttp.registerGUIObserver(GUI_OBSERVER_URL)
    val gui = new GUI
    val server = Http()
      .newServerAt(GUI_HOST, GUI_PORT)
      .bind(routes(GUIRoutes(gui)))
    CoordinatedShutdown(system).addJvmShutdownHook(shutdown)
    gui.run

  private def routes(guiRoutes: GUIRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("gui") {
          concat(
            guiRoutes.guiRoutes
          )
        }
      )
    }

  private def shutdown: Future[Unit] =
    logger.info("GUI Service -- Shutting Down Http Server...")
    CoreRequestHttp.deregisterGUIObserver(GUI_OBSERVER_URL)
