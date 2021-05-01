package rerorero.kafka.streams.example

import org.slf4j.LoggerFactory

trait Logging {
  protected[this] val loggerName = this.getClass.getName
  protected[this] lazy val logger = LoggerFactory.getLogger(loggerName)
}
