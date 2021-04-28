package rerorero.kafka.connect.example

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.{SourceRecord, SourceTask}

import scala.jdk.CollectionConverters._

class StringGenSourceTask extends SourceTask with Logging {
  private[this] var config: StringGenSourceConfig = _

  override def start(props: java.util.Map[String,String]): Unit = {
    logger.info("source task started")
    config = new StringGenSourceConfig(props)
  }

  override def poll(): java.util.List[SourceRecord] = {
    logger.info("poll occurred")
    val currentTime = System.currentTimeMillis()
    val key = s"${currentTime % 3}"
    val sourcePartition = Map("key"->key).asJava
    val sourceOffset = Map("ts"->s"${currentTime}").asJava

    val offset = context.offsetStorageReader().offset(sourcePartition)
    logger.info(s"offset=${offset.asScala}")

    val record = new SourceRecord(sourcePartition, sourceOffset, config.topic,
      Schema.STRING_SCHEMA, key, Schema.STRING_SCHEMA, config.text)
    logger.info(s"generated SourceRecord(key=${key},offset=${currentTime})")

    synchronized {
      wait(1000)
    }
    logger.info("poll sleeping ends")
    Seq(record).asJava
  }

  override def stop(): Unit = {
    logger.info("source task stopped")
  }

  override def version(): String = getClass.getPackage.getImplementationVersion
}
