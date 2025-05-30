val thisVersion = "1.0.0-SNAPSHOT"
val scala3Version = "3.6.4"
val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"

ThisBuild / version := thisVersion
ThisBuild / scalaVersion := scala3Version

lazy val root = (project in file("."))
  .settings(
    name := "dotsandboxes-logging",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.5.2",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.4",
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion cross CrossVersion.for3Use2_13,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion cross CrossVersion.for3Use2_13,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion cross CrossVersion.for3Use2_13,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test cross CrossVersion.for3Use2_13,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test cross CrossVersion.for3Use2_13,
      "com.lightbend.akka" %% "akka-stream-alpakka-file" % "6.0.2" cross CrossVersion.for3Use2_13
    )
  )
