package metric.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig._
import metric.api.routes.MetricRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object MetricHttpServer:
  private[server] implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run: Future[ServerBinding] =
    val serverBinding = Http()
      .newServerAt(METRIC_HOST, METRIC_PORT)
      .bind(routes(new MetricRoutes))

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-server") { () =>
      shutdown(serverBinding)
    }

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Metric Service -- Http Server is running at $METRIC_BASE_URL\n")
      case Failure(exception) => logger.error(s"Metric Service -- Http Server failed to start", exception)
    }
    serverBinding

  private def routes(metricRoutes: MetricRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("metric") {
          concat(
            metricRoutes.metricRoutes
          )
        }
      )
    }

  private def shutdown(serverBinding: Future[ServerBinding]): Future[Done] =
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Metric Service -- Shutting Down Http Server...")
        system.terminate()
        Done
      }
    }
