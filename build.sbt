import sbt.Keys._

name := "slogr"
organization := "com.uniformlyrandom"
val _scalaVersion = "2.12.4"

scalaVersion := _scalaVersion

lazy val slogr = crossProject
  .in(file("."))
  .settings(
    organization := "com.uniformlyrandom",
    name := "slogr",
    version := "0.3",
    scalaVersion := _scalaVersion,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value)
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.21"
    ),
    resolvers ++= Seq(
      "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
    )
  )
  .jsSettings(
    emitSourceMaps := true,
    libraryDependencies ++= Seq(),
    jsDependencies ++= Seq()
  )

lazy val slogrJS = slogr.js
lazy val slogrJVM = slogr.jvm
