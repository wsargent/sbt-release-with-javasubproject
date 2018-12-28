lazy val scala212 = "2.12.8"
lazy val scala211 = "2.11.12"
lazy val bothScalaVersions = List(scala212, scala211)

ThisBuild / scalaVersion := scala212

val disableDocs = Seq[Setting[_]](
  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false
)

val disablePublishing = Seq[Setting[_]](
  publishArtifact := false,
  skip in publish := true
)

val javaOnly = Seq[Setting[_]](
  // set crossScalaVersions to just one scalaVersion
  crossScalaVersions := List(scala212),
  crossPaths := false,
  autoScalaLibrary := false
)

lazy val `java-project` = (project in file("java-project"))
  .settings(disableDocs)
  .settings(javaOnly)

lazy val `scala-project` = (project in file("scala-project"))
  .dependsOn(`java-project`)
  .settings(disableDocs)
  .settings(
    crossScalaVersions := bothScalaVersions
  )

import ReleaseTransformations._
lazy val root = (project in file("."))
  .settings(disablePublishing)
  .settings(
    name := "publish-with-java-project",
    //logLevel := Level.Debug,
    releaseCrossBuild := true, // must be set in root project
    crossScalaVersions := Nil, // set crossScalaVersions to Nil
  )
  .aggregate(
    `java-project`,
    `scala-project`
  )
