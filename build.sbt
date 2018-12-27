
val disableDocs = Seq[Setting[_]](
  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false
)

val disablePublishing = Seq[Setting[_]](
  publishArtifact := false,
  skip in publish := true
)

val javaOnly = Seq[Setting[_]](
  // DO NOT SET crossScalaVersions in java project
  crossPaths := false,
  autoScalaLibrary := false
)

lazy val onlyOnce = new java.util.concurrent.CountDownLatch(1)
def countDown: Boolean = {
  val count = onlyOnce.getCount
  onlyOnce.countDown()
  count == 0
}

lazy val `java-project` = (project in file("java-project"))
  .settings(disableDocs)
  .settings(javaOnly)
  .settings(
    skip in publish := countDown // publish only once.
  )

lazy val `scala-project` = (project in file("scala-project"))
  .settings(disableDocs)
  .dependsOn(`java-project`)
  .aggregate(`java-project`)

import ReleaseTransformations._
lazy val root = (project in file("."))
  .settings(disablePublishing)
  .settings(
    name := "publish-with-java-project",
    //logLevel := Level.Debug,
    releaseCrossBuild := true, // must be set in root project
    crossScalaVersions := Seq("2.11.6", scalaVersion.value), // set in root project
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,              // : ReleaseStep
      inquireVersions,                        // : ReleaseStep
      runClean,                               // : ReleaseStep
      runTest,                                // : ReleaseStep
      setReleaseVersion,                      // : ReleaseStep
      commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
      tagRelease,                             // : ReleaseStep
      publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
      setNextVersion,                         // : ReleaseStep
      commitNextVersion                       // : ReleaseStep
      //pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
    )
  )
  .aggregate(
    `java-project`,
    `scala-project`
  )
