import sbtcrossproject.CrossPlugin.autoImport.crossProject

val _scalaVersion = "2.12.12"
val _organization = "com.uniformlyrandom"
val _version = "0.4.0"

version := _version
scalaVersion := _scalaVersion
organization := _organization

skip in publish := true

lazy val slogr = crossProject(JSPlatform, JVMPlatform)
  .settings(
    organization := _organization,
    name := "slogr",
    version := _version,
    scalacOptions += "-feature",
    homepage := Some(url("http://www.uniformlyrandom.com")),
    licenses := Seq(("MIT", url("http://opensource.org/licenses/mit-license.php"))),
    //    scalacOptions ++= Seq("-Ymacro-debug-lite"),
    scalaVersion := _scalaVersion,
    // Sonatype
    publishArtifact in Test := false,
    publishTo := sonatypePublishToBundle.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    pomExtra :=
      <scm>
        <url>git@github.com:uniformlyrandom/scron.git</url>
        <connection>scm:git:git@github.com:uniformlyrandom/scron.git</connection>
      </scm>
        <developers>
          <developer>
            <id>romansky</id>
            <name>Roman Landenband</name>
            <url>http://www.uniformlyrandom.com</url>
          </developer>
        </developers>,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
    )
    // publish Github sources
  )
  .settings(xerial.sbt.Sonatype.sonatypeSettings: _*)
  .jsSettings(
    libraryDependencies ++= Seq(),
    scalacOptions ++= (
      if (isSnapshot.value) Seq.empty
      else
        Seq({
          val a = baseDirectory.value.toURI.toString
            .replaceFirst("[^/]+/?$", "")
          val g =
            "https://raw.githubusercontent.com/japgolly/scalajs-react"
          s"-P:scalajs:mapSourceURI:$a->$g/v${version.value}/"
        }))
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.30"
    ),
    resolvers ++= Seq(
      "Typesafe Repo" at "https://repo.typesafe.com/typesafe/releases/"
    )
  )

lazy val slogrJS = slogr.js
lazy val slogrJVM = slogr.jvm
