package rerorero.kafka.connect.example

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.source.SourceConnector

import scala.jdk.CollectionConverters._

class StringGenSourceConnector extends SourceConnector with Logging {
  private[this] var config: StringGenSourceConfig = _

  override def start(props: java.util.Map[String, String]): Unit = {
    logger.info("start source connector")
    config = new StringGenSourceConfig(props)
  }

  override def taskClass(): Class[_ <: Task] = classOf[StringGenSourceTask]

  override def taskConfigs(maxTasks: Int): java.util.List[java.util.Map[String,String]] =
    List.fill(maxTasks)(config.props).asJava

  override def stop(): Unit = logger.info("stop source connector")

  override def config(): ConfigDef = StringGenSourceConfig.DEF

  override def version(): String = getClass.getPackage.getImplementationVersion
}