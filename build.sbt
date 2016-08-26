name := """streams-playground"""

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "io.humble" % "humble-video-all" % "0.2.1",
  "com.typesafe.akka" %% "akka-stream" % "2.4.9",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.9",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test")

