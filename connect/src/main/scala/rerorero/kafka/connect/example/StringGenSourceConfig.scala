package rerorero.kafka.connect.example

import org.apache.kafka.common.config.ConfigDef.{Importance, NO_DEFAULT_VALUE, Type}
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}

class StringGenSourceConfig(
  val props: java.util.Map[String,String]
) extends AbstractConfig(StringGenSourceConfig.DEF, props){
  val text = getString(StringGenSourceConfig.text)
  val topic = getString(StringGenSourceConfig.topic)
}

object StringGenSourceConfig {
  val text = "example.source.text"
  val topic = "example.source.topic"
  val DEF: ConfigDef = new ConfigDef()
    .define(topic, Type.STRING, NO_DEFAULT_VALUE, Importance.HIGH, "topic name")
    .define(text, Type.STRING, "yo", Importance.HIGH, "text to be sent")
}
