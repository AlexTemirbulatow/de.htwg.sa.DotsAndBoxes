val scala3Version = "3.5.0"

lazy val dependencies = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := scala3Version,
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0").cross(CrossVersion.for3Use2_13),
  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.10.0-RC5"),
  libraryDependencies += "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % "test"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotsandboxes",
    dependencies
  )
  .dependsOn(util, core, model, computer, persistence, gui, tui)
  .aggregate(util, core, model, computer, persistence, gui, tui)

lazy val util = project
  .in(file("util"))
  .settings(
    name := "util",
    dependencies
  )

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    dependencies
  )
  .dependsOn(util, model, computer, persistence)

lazy val model = project
  .in(file("model"))
  .settings(
    name := "model",
    dependencies
  )
  .dependsOn(util)

lazy val computer = project
  .in(file("computer"))
  .settings(
    name := "computer",
    dependencies
  )
  .dependsOn(util, model)

lazy val persistence = project
  .in(file("persistence"))
  .settings(
    name := "persistence",
    dependencies
  )
  .dependsOn(util, model)

lazy val gui = project
  .in(file("gui"))
  .settings(
    name := "gui",
    dependencies
  )
  .dependsOn(util, core)

lazy val tui = project
  .in(file("tui"))
  .settings(
    name := "tui",
    dependencies
  )
  .dependsOn(util, core)

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
