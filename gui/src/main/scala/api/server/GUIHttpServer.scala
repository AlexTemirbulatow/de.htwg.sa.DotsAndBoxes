package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.GUIRoutes
import api.service.CoreRequestHttp
import common.config.ServiceConfig.{GUI_BASE_URL, GUI_HOST, GUI_OBSERVER_URL, GUI_PORT}
import guiComponent.GUI
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GUIHttpServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Future[ServerBinding] =
    CoreRequestHttp.registerGUIObserver(GUI_OBSERVER_URL)
    val gui = new GUI
    val serverBinding = Http()
      .newServerAt(GUI_HOST, GUI_PORT)
      .bind(routes(GUIRoutes(gui)))

    CoordinatedShutdown(system).addJvmShutdownHook(shutdown(serverBinding))

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"GUI Service -- Http Server is running at $GUI_BASE_URL\n")
      case Failure(exception) => logger.error(s"GUI Service -- Http Server failed to start", exception)
    }

    gui.run
    serverBinding

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

  private def shutdown(serverBinding: Future[ServerBinding]): Unit =
    serverBinding
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("GUI Service -- Shutting Down Http Server...")
        system.terminate()
      }
    CoreRequestHttp.deregisterGUIObserver(GUI_OBSERVER_URL)
