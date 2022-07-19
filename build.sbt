ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.1"

ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

val flinkVersion = "1.14.5"

val flinkDependencies = List(
  "org.apache.flink" %% "flink-clients" % flinkVersion,
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion)

val projectDependencies = List(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
  "org.slf4j" % "slf4j-simple" % "1.7.36")

lazy val root = (project in file("."))
  .settings(
    name := "scala_playground",
    libraryDependencies ++= flinkDependencies ::: projectDependencies,
    inThisBuild(List())
  )

Compile / mainClass := Some("main.Playground")