scalaVersion := "2.13.5"
name := "kafka-connect-scala-example"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "connect-api" % "2.8.0",
)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
)

assembly / assemblyJarName := "kafka-connect-example.jar"