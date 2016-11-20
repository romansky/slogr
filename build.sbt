name := "slogr"
organization := "com.uniformlyrandom.slogr"
version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
)