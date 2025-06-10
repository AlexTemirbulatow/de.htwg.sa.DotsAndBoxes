package logging.config

object Config:
  val LOGGING_FILE_PATH           = "logs/application.log"
  val BOOTSTRAP_SERVER_ADDRESS    = "localhost:9092"
  val KAFKA_LOGGING_GROUP_IP      = "logging"
  val KAFKA_LOGGING_TOPIC         = "logs"
  val KAFKA_LOGGING_METRIC_TOPIC  = "log-metric"
