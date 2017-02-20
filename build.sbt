name := "slogr"
organization := "com.uniformlyrandom"
version := "0.2"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
)
