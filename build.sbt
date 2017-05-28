name := "ivanSusanin"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.6")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
