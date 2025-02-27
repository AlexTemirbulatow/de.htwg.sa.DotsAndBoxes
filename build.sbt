import org.scoverage.coveralls.GitHubActions

val scala3Version = "3.5.0"

lazy val commonSettings = Seq(
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
    commonSettings,
    coverageAggregate / aggregate := true
  )
  .dependsOn(util, core, model, computer, persistence, gui, tui)
  .aggregate(util, core, model, computer, persistence, gui, tui)

lazy val util = project
  .in(file("util"))
  .settings(
    name := "util",
    commonSettings
  )

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    commonSettings
  )
  .dependsOn(util, model, computer, persistence)
  .aggregate(util, model, computer, persistence)

lazy val model = project
  .in(file("model"))
  .settings(
    name := "model",
    commonSettings
  )
  .dependsOn(util)
  .aggregate(util)

lazy val computer = project
  .in(file("computer"))
  .settings(
    name := "computer",
    commonSettings
  )
  .dependsOn(util, model)
  .aggregate(util, model)

lazy val persistence = project
  .in(file("persistence"))
  .settings(
    name := "persistence",
    commonSettings
  )
  .dependsOn(util, model)
  .aggregate(util, model)

lazy val gui = project
  .in(file("gui"))
  .settings(
    name := "gui",
    commonSettings
  )
  .dependsOn(util, core)
  .aggregate(util, core)

lazy val tui = project
  .in(file("tui"))
  .settings(
    name := "tui",
    commonSettings
  )
  .dependsOn(util, core)
  .aggregate(util, core)

import org.scoverage.coveralls.Imports.CoverallsKeys.*

coverallsTokenFile := sys.env.get("COVERALLS_REPO_TOKEN")
coverallsService := "github-actions"

coverageHighlighting := true
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 0
coverageMinimumBranchTotal := 0
coverageMinimumStmtPerPackage := 0
coverageMinimumBranchPerPackage := 0
coverageMinimumStmtPerFile := 0
coverageMinimumBranchPerFile := 0
