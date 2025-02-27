val scala3Version = "3.5.0"

lazy val dependencies = Seq(
  scalaVersion := scala3Version,
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
  .cross(CrossVersion.for3Use2_13),
  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.10.0-RC5"),
  libraryDependencies += "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % "test"
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotsandboxes",
    version := "0.1.0-SNAPSHOT",
    dependencies
  )
  .dependsOn(util, core, model, computer, persistence, gui, tui)
  .aggregate(util, core, model, computer, persistence, gui, tui)

lazy val util        = project in file("util")
lazy val core        = project in file("core")
lazy val model       = project in file("model")
lazy val computer    = project in file("computer")
lazy val persistence = project in file("persistence")
lazy val gui         = project in file("gui")
lazy val tui         = project in file("tui")


import org.scoverage.coveralls.GitHubActions
import org.scoverage.coveralls.Imports.CoverallsKeys.*

coverallsTokenFile := sys.env.get("COVERALLS_REPO_TOKEN")
coverallsService := Some(GitHubActions)

coverageHighlighting := true
coverageFailOnMinimum := false
coverageMinimumStmtTotal := 0
coverageMinimumBranchTotal := 0
coverageMinimumStmtPerPackage := 0
coverageMinimumBranchPerPackage := 0
coverageMinimumStmtPerFile := 0
coverageMinimumBranchPerFile := 0
