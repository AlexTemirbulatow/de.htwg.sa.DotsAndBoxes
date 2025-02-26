val scala3Version = "3.5.0"

lazy val commonSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := scala3Version,
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
  .cross(CrossVersion.for3Use2_13),
  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.10.0-RC5"),
  libraryDependencies += "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % "test",
  jacocoReportSettings := JacocoReportSettings(
    "Jacoco Coverage Report",
    None,
    JacocoThresholds(),
    Seq(
      JacocoReportFormats.ScalaHTML,
      JacocoReportFormats.XML,
    ),
    "utf-8"
  ),
  jacocoCoverallsServiceName := "github-actions",
  jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
  jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
  jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN"),
  Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.ScalaLibrary
)

lazy val util = project
  .in(file("util"))
  .settings(
    name := "util",
    commonSettings
  )
  .enablePlugins(JacocoPlugin)

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    commonSettings
  )
  .dependsOn(util, model, computer, persistence)
  .enablePlugins(JacocoPlugin)

lazy val model = project
  .in(file("model"))
  .settings(
    name := "model",
    commonSettings
  )
  .dependsOn(util)
  .enablePlugins(JacocoPlugin)

lazy val computer = project
  .in(file("computer"))
  .settings(
    name := "computer",
    commonSettings
  )
  .dependsOn(util, model)
  .enablePlugins(JacocoPlugin)

lazy val persistence = project
  .in(file("persistence"))
  .settings(
    name := "persistence",
    commonSettings
  )
  .dependsOn(util, model)
  .enablePlugins(JacocoPlugin)

lazy val gui = project
  .in(file("gui"))
  .settings(
    name := "gui",
    commonSettings
  )
  .dependsOn(util, core, computer)
  .enablePlugins(JacocoPlugin)

lazy val tui = project
  .in(file("tui"))
  .settings(
    name := "tui",
    commonSettings
  )
  .dependsOn(util, core)
  .enablePlugins(JacocoPlugin)

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotsandboxes",
    commonSettings
  )
  .enablePlugins(JacocoPlugin, JacocoCoverallsPlugin)
  .aggregate(util, core, model, computer, persistence, gui, tui)
