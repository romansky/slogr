import sbtcrossproject.CrossPlugin.autoImport.crossProject

val _scalaVersion = "2.13.7"
val _organization = "com.uniformlyrandom"
val _version = "0.5.0"

version := _version
scalaVersion := _scalaVersion
organization := _organization

publish / skip := true

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

lazy val slogr = crossProject(JSPlatform, JVMPlatform)
  .settings(
    organization := _organization,
    name := "slogr",
    version := _version,
    scalacOptions += "-feature",
    homepage := Some(url("https://www.uniformlyrandom.com")),
    licenses := Seq(("MIT", url("https://opensource.org/licenses/mit-license.php"))),
    //    scalacOptions ++= Seq("-Ymacro-debug-lite"),
    scalaVersion := _scalaVersion,
    // Sonatype
    Test / publishArtifact  := false,
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
  )
  .settings(xerial.sbt.Sonatype.sonatypeSettings: _*)
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.32"
    ),
    resolvers ++= Seq(
      "Typesafe Repo" at "https://repo.typesafe.com/typesafe/releases/"
    )
  )

lazy val slogrJS = slogr.js
lazy val slogrJVM = slogr.jvm
