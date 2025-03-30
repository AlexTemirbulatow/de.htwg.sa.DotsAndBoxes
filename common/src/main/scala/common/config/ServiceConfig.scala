package common.config

object ServiceConfig:
  val MODEL_HOST = "localhost"
  val MODEL_PORT = 8080
  val MODEL_BASE_URL = s"http://$MODEL_HOST:$MODEL_PORT/"

  val PERSISTENCE_HOST = "localhost"
  val PERSISTENCE_PORT = 8081
  val PERSISTENCE_BASE_URL = s"http://$PERSISTENCE_HOST:$PERSISTENCE_PORT/"

  val COMPUTER_HOST = "localhost"
  val COMPUTER_PORT = 8082
  val COMPUTER_BASE_URL = s"http://$COMPUTER_HOST:$COMPUTER_PORT/"

  val CORE_HOST = "localhost"
  val CORE_PORT = 8083
  val CORE_BASE_URL = s"http://$CORE_HOST:$CORE_PORT/"

  val TUI_HOST = "localhost"
  val TUI_PORT = 8084
  val TUI_OBSERVER_URL = s"http://$TUI_HOST:$TUI_PORT/api/tui/update"

  val GUI_HOST = "localhost"
  val GUI_PORT = 8085
  val GUI_OBSERVER_URL = s"http://$GUI_HOST:$GUI_PORT/api/gui/update"
