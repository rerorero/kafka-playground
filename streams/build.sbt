scalaVersion := "2.13.5"
name := "kafka-streams-scala-example"
val kafkaVersion = "2.8.0"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
)

assembly / assemblyJarName := "kafka-streams-example.jar"
assemblyMergeStrategy in assembly := {
  case "module-info.class" => MergeStrategy.discard // intended to be compiled with jdk8
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}