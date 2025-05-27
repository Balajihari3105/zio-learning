ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "zio-learning",
    Compile / run / mainClass := Some("envargs.MainApp"),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.1.12", // Use consistent version
      "dev.zio" %% "zio-json" % "0.6.2",
      "dev.zio" %% "zio-streams" % "2.1.12",
      "com.cronutils" % "cron-utils" % "9.2.1",
      "org.playframework" %% "play-ahc-ws-standalone" % "3.1.0-M2",
      "com.typesafe.play" %% "play" % "2.9.5",
      "io.grpc" % "grpc-netty" % "1.65.1",
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      "dev.zio" %% "zio-config" % "4.0.2",
      "dev.zio" %% "zio-config-typesafe" % "4.0.2",
      "com.typesafe" % "config" % "1.4.3", // For HOCON support
      "dev.zio" %% "zio-config-magnolia" % "4.0.2",
      "org.playframework" %% "play-ws-standalone" % "3.1.0-M4",
      "dev.zio" %% "zio-http" % "0.0.5",
      "org.scalatest" %% "scalatest" % "3.2.18"

    )
  )
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6.0"
libraryDependencies += "org.typelevel" %% "cats-effect-cps" % "0.4.0"

libraryDependencies += "dev.zio" %% "zio-kafka" % "2.9.0"


// https://mvnrepository.com/artifact/org.scala-lang/scala-reflect
libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.13.16"

PB.targets in Compile := Seq(
  scalapb.gen(grpc = true) -> (sourceManaged in Compile).value / "scalapb",
  scalapb.zio_grpc.ZioCodeGenerator -> (sourceManaged in Compile).value / "scalapb"
)

// https://mvnrepository.com/artifact/org.typelevel/cats-core
libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0"
