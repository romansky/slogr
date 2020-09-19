import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}


name := "slogr"
organization := "com.uniformlyrandom"
val _scalaVersion = "2.12.12"

scalaVersion := _scalaVersion

lazy val slogr = crossProject(JSPlatform, JVMPlatform)
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
      "org.slf4j" % "slf4j-api" % "1.7.30"
    ),
    resolvers ++= Seq(
      "Typesafe Repo" at "https://repo.typesafe.com/typesafe/releases/"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(),
  )

lazy val slogrJS = slogr.js
lazy val slogrJVM = slogr.jvm
