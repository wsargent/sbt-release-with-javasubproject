import sbt.Keys._
import sbt._

object CommonSettings extends AutoPlugin {
  override def trigger = allRequirements

  val javacSettings = Seq(
    "-source",
    "1.8",
    "-target",
    "1.8",
    "-Xlint:deprecation",
    "-Xlint:unchecked"
  )

  override lazy val projectSettings = Seq(
    // Make sure we publish locally so we don't scribble over the data
    publishMavenStyle := true,
    //isSnapshot := false,
    javacOptions in (Compile, doc) ++= javacSettings,
    javacOptions in Test ++= javacSettings,
    publishConfiguration := publishConfiguration.value.withOverwrite(false),
    publishTo := Some(
      Resolver.file("file", new File(baseDirectory.value + "/release-repo"))),
    // docker pull docker.bintray.io/jfrog/artifactory-oss:latest
    // docker run --name artifactory -d -p 8081:8081 docker.bintray.io/jfrog/artifactory-oss:latest
    // open browser to http://localhost:8081/
    // skip password, set up sbt repo.
    // https://www.jfrog.com/confluence/display/RTF/SBT+Repositories#SBTRepositories-DeployingArtifacts
    // admin / password
    //  publishTo := Some ("Artifactory Realm" at "http://localhost:8081/artifactory/sbt-release-local"),
    credentials += Credentials("Artifactory Realm",
                               "localhost",
                               "admin",
                               "password"),
  )
}
