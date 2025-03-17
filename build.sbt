val scala3Version = "3.6.4"
val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"
val dotsandboxesLibVersion = "0.1.3-SNAPSHOT"

lazy val commonSettings = Seq(
  resolvers += "Github Packages" at "https://maven.pkg.github.com/AlexTemirbulatow/de.htwg.sa.DotsAndBoxes.library",
  credentials += Credentials(
    "GitHub Package Registry",
    "maven.pkg.github.com",
    sys.env.getOrElse("GITHUB_USERNAME", ""),
    sys.env.getOrElse("GITHUB_TOKEN", "")
  ),
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.2.14",
    "org.scalatest" %% "scalatest" % "3.2.14" % Test,
    "org.scala-lang.modules" %% "scala-swing" % "3.0.0" cross CrossVersion.for3Use2_13,
    "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
    "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
    "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test,
    "ch.qos.logback" % "logback-classic" % "1.5.2",
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.github.AlexTemirbulatow" %% "dotsandboxes" % dotsandboxesLibVersion
  )
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotsandboxes",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
  )
  .settings(commonSettings)
  .aggregate(core, model, computer, persistence, gui, tui)

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )
  .settings(commonSettings)
  .dependsOn(model, computer, persistence)

lazy val model = project
  .in(file("model"))
  .settings(
    name := "model",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )
  .settings(commonSettings)

lazy val computer = project
  .in(file("computer"))
  .settings(
    name := "computer",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )
  .settings(commonSettings)
  .dependsOn(model)

lazy val persistence = project
  .in(file("persistence"))
  .settings(
    name := "persistence",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )
  .settings(commonSettings)
  .dependsOn(model)

lazy val gui = project
  .in(file("gui"))
  .settings(
    name := "gui",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )
  .settings(commonSettings)
  .dependsOn(core)

lazy val tui = project
  .in(file("tui"))
  .settings(
    name := "tui",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )
  .settings(commonSettings)

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
