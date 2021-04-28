scalaVersion := "2.13.5"
name := "kafka-connect-scala-example"
val kafkaVersion = "2.8.0"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "connect-api" % kafkaVersion,
  "org.apache.kafka" % "connect-transforms" % kafkaVersion,
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
)

assembly / assemblyJarName := "kafka-connect-example.jar"