val thisVersion = "1.0.0-SNAPSHOT"
val scala3Version = "3.6.4"
val scalatestVersion = "3.2.14"
val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"
val circeVersion = "0.14.1"

val dotsandboxesLibVersion = "1.0.6"
val dotsandboxesLibUrl = "https://maven.pkg.github.com/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes.library"

ThisBuild / version := thisVersion
ThisBuild / scalaVersion := scala3Version
ThisBuild / resolvers += "Github Packages" at dotsandboxesLibUrl
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  sys.env.getOrElse("GITHUB_USERNAME", ""),
  sys.env.getOrElse("GITHUB_TOKEN", "")
)

ThisBuild / libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % scalatestVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0" cross CrossVersion.for3Use2_13,
  "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.2",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.4",
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.wiremock" % "wiremock" % "3.12.1" % Test,
  "com.typesafe.slick" %% "slick" % "3.6.0" cross CrossVersion.for3Use2_13,
  "org.postgresql" % "postgresql" % "42.7.3",
  "com.h2database" % "h2" % "2.3.232",
  "com.github.AlexTemirbulatow" %% "dotsandboxes" % dotsandboxesLibVersion
)

ThisBuild / Test / parallelExecution := false
ThisBuild / Test / fork := true

lazy val root = project
  .in(file("."))
  .settings(name := "dotsandboxes")
  .aggregate(common, core, model, computer, persistence, gui, tui)

lazy val common = project
  .in(file("common"))
  .settings(name := "common")

lazy val model = project
  .in(file("model"))
  .settings(name := "model")
  .dependsOn(common)

lazy val persistence = project
  .in(file("persistence"))
  .settings(name := "persistence")
  .dependsOn(common)
  .dependsOn(model % Test)

lazy val computer = project
  .in(file("computer"))
  .settings(name := "computer")
  .dependsOn(common)
  .dependsOn(model % Test)

lazy val core = project
  .in(file("core"))
  .settings(name := "core")
  .dependsOn(common, model)
  .dependsOn(persistence % Test, computer % Test)

lazy val tui = project
  .in(file("tui"))
  .settings(name := "tui")
  .dependsOn(common)

lazy val gui = project
  .in(file("gui"))
  .settings(name := "gui")
  .dependsOn(common)

import org.scoverage.coveralls.Imports.CoverallsKeys._
coverallsTokenFile := sys.env.get("COVERALLS_REPO_TOKEN")
coverallsService := Some(org.scoverage.coveralls.GitHubActions)
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 0
coverageMinimumBranchTotal := 0
coverageMinimumStmtPerPackage := 0
coverageMinimumBranchPerPackage := 0
coverageMinimumStmtPerFile := 0
coverageMinimumBranchPerFile := 0
