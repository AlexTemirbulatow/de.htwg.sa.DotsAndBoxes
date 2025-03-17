val scala3Version = "3.5.0"
val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"

lazy val dependencies = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := scala3Version,
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13),
  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.10.0-RC5"),
  libraryDependencies += "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % "test",
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.2",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotsandboxes",
    dependencies
  )
  .dependsOn(core, model, computer, persistence, gui, tui)
  .aggregate(core, model, computer, persistence, gui, tui)

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    dependencies
  )
  .dependsOn(model, computer, persistence)

lazy val model = project
  .in(file("model"))
  .settings(
    name := "model",
    dependencies
  )

lazy val computer = project
  .in(file("computer"))
  .settings(
    name := "computer",
    dependencies
  )
  .dependsOn(model)

lazy val persistence = project
  .in(file("persistence"))
  .settings(
    name := "persistence",
    dependencies
  )
  .dependsOn(model)

lazy val gui = project
  .in(file("gui"))
  .settings(
    name := "gui",
    dependencies
  )
  .dependsOn(core)

lazy val tui = project
  .in(file("tui"))
  .settings(
    name := "tui",
    dependencies
  )
  .dependsOn(core)

import org.scoverage.coveralls.Imports.CoverallsKeys.*

coverallsTokenFile := sys.env.get("COVERALLS_REPO_TOKEN")
coverallsService := Some(org.scoverage.coveralls.GitHubActions)

coverageHighlighting := true
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 0
coverageMinimumBranchTotal := 0
coverageMinimumStmtPerPackage := 0
coverageMinimumBranchPerPackage := 0
coverageMinimumStmtPerFile := 0
coverageMinimumBranchPerFile := 0
