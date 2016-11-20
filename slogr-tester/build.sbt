name := "slogr-tester"

version := "0.1"

scalaVersion := "2.11.8"

val _log4jVersion = "2.6.1"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j" % _log4jVersion,
  "org.apache.logging.log4j" % "log4j-api" % _log4jVersion,
  "org.apache.logging.log4j" % "log4j-core" % _log4jVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % _log4jVersion
)

lazy val slogr = ProjectRef(file("../"), "slogr")

lazy val `slogr-tester` = (project in file(".")) dependsOn slogr
