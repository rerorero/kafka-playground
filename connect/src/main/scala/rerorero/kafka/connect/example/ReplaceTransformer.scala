package rerorero.kafka.connect.example

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigDef.{Importance, Type}
import org.apache.kafka.connect.connector.ConnectRecord
import org.apache.kafka.connect.transforms.Transformation
import org.apache.kafka.connect.transforms.util.SimpleConfig

import java.util

class ReplaceTransformer[R <: ConnectRecord[R]] extends Transformation[R] with Logging {
  private[this] var config: ReplaceTransformerConfig = _

  override def apply(record: R): R = {
    val value = record.value().toString
    val newValue = value.replace(config.target, config.replacement)
    logger.info(s"smt applied: ${value} -> ${newValue}")
    record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), record.valueSchema(), newValue, record.timestamp())
  }

  override def config(): ConfigDef = ReplaceTransformerConfig.DEF

  override def close(): Unit =
    logger.info("smt closed")

  override def configure(configs: util.Map[String, _]): Unit =
    config = new ReplaceTransformerConfig(configs)
}

class ReplaceTransformerConfig(props: java.util.Map[String, _])
  extends SimpleConfig(ReplaceTransformerConfig.DEF, props) {
  val target = getString(ReplaceTransformerConfig.target)
  val replacement = getString(ReplaceTransformerConfig.replacement)
}

object ReplaceTransformerConfig {
  val target = "example.target"
  val replacement = "example.replacement"
  val DEF = new ConfigDef()
    .define(target, Type.STRING, Importance.HIGH, "target to be replaced")
    .define(replacement, Type.STRING, Importance.HIGH, "value")
}
