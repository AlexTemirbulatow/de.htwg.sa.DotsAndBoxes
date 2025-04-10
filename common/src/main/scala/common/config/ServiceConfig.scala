package common.config

import com.typesafe.config.{Config, ConfigFactory}

object ServiceConfig:
  private val config: Config = ConfigFactory.load()

  val MODEL_HOST               = bindHost("model")
  val MODEL_PORT               = port("model")
  val MODEL_BASE_URL           = baseUrl("model")
  val MODEL_SERVICE_URL        = serviceUrl("model")

  val PERSISTENCE_HOST         = bindHost("persistence")
  val PERSISTENCE_PORT         = port("persistence")
  val PERSISTENCE_BASE_URL     = baseUrl("persistence")
  val PERSISTENCE_SERVICE_URL  = serviceUrl("persistence")
  val FILEIO_FILENAME          = "field"

  val COMPUTER_HOST            = bindHost("computer")
  val COMPUTER_PORT            = port("computer")
  val COMPUTER_BASE_URL        = baseUrl("computer")
  val COMPUTER_SERVICE_URL     = serviceUrl("computer")
  val COMPUTER_SLEEP_TIME      = 1000

  val CORE_HOST                = bindHost("core")
  val CORE_PORT                = port("core")
  val CORE_BASE_URL            = baseUrl("core")
  val CORE_SERVICE_URL         = serviceUrl("core")

  val TUI_HOST                 = bindHost("tui")
  val TUI_PORT                 = port("tui")
  val TUI_BASE_URL             = baseUrl("tui")
  val TUI_OBSERVER_URL         = observerUrl("tui")

  val GUI_HOST                 = bindHost("gui")
  val GUI_PORT                 = port("gui")
  val GUI_BASE_URL             = baseUrl("gui")
  val GUI_OBSERVER_URL         = observerUrl("gui")

  private def bindHost(service: String): String =
    config.getString(s"$service.bindHost")

  private def remoteHost(service: String): String =
    config.getString(s"$service.remoteHost")

  private def port(service: String): Int =
    config.getInt(s"$service.port")

  private def baseUrl(service: String): String =
    s"http://${bindHost(service)}:${port(service)}/"

  private def serviceUrl(service: String): String =
    s"http://${remoteHost(service)}:${port(service)}/"

  private def observerUrl(service: String): String =
    serviceUrl(service).concat(s"api/$service/update")
