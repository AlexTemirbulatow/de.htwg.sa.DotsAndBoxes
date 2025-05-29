package logging.loggingComponent

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.stream.scaladsl._
import common.config.ServiceConfig.LOGGING_FILE_PATH
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.NotUsed
import org.slf4j.LoggerFactory
import akka.actor.CoordinatedShutdown

object LogFileReader:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private implicit val materializer: Materializer = Materializer(system)

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def startLiveMonitoring =
    val source: Source[String, NotUsed] = FileTailSource.lines(
      path = Paths.get(LOGGING_FILE_PATH),
      maxLineSize = 8192,
      pollingInterval = 250.millis,
      lf = "\n",
      charset = StandardCharsets.UTF_8
    )

    val warnFlow: Flow[String, String, NotUsed] =
      Flow[String].filter(_.contains("WARN"))

    val errorFlow: Flow[String, String, NotUsed] =
      Flow[String].filter(_.contains("ERROR"))

    val mergedSink: Sink[String, ?] = Sink.foreach[String](logger.info(_))

    val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val input = builder.add(source)
      val warnFilter = builder.add(warnFlow)
      val errorFilter = builder.add(errorFlow)
      val output = builder.add(mergedSink)

      val broadcast = builder.add(Broadcast[String](2))
      val merge = builder.add(Merge[String](2))

      input ~> broadcast

      broadcast.out(0) ~> warnFilter  ~> merge.in(0)
      broadcast.out(1) ~> errorFilter ~> merge.in(1)
      
      merge.out ~> output

      ClosedShape
    })

    graph.run()
