val scala3Version = "3.1.3"
val scalaTestVersion = "3.2.14"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "dev.atedeg.mdm"
ThisBuild / homepage := Some(url("https://github.com/atedeg/mdm"))
ThisBuild / licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / versionScheme := Some("early-semver")

ThisBuild / Docker / dockerUsername := Some("atedeg")

ThisBuild / developers := List(
  Developer(
    "giacomocavalieri",
    "Giacomo Cavalieri",
    "giacomo.cavalieri@icloud.com",
    url("https://github.com/giacomocavalieri"),
  ),
  Developer(
    "nicolasfara",
    "Nicolas Farabegoli",
    "nicolas.farabegoli@gmail.com",
    url("https://github.com/nicolasfara"),
  ),
  Developer(
    "ndido98",
    "Nicolò Di Domenico",
    "ndido98@gmail.com",
    url("https://github.com/ndido98"),
  ),
  Developer(
    "vitlinda",
    "Linda Vitali",
    "lindav94vitali@gmail.com",
    url("https://github.com/vitlinda"),
  ),
)

// ThisBuild / wartremoverErrors ++= Warts.allBut(Wart.Overloading, Wart.Equals)

ThisBuild / scalafixDependencies ++= Seq(
  "com.github.xuwei-k" %% "scalafix-rules" % "0.2.11",
)

ThisBuild / semanticdbEnabled := true
ThisBuild / scalacOptions ++= Seq("-language:implicitConversions", "-feature", "-Xfatal-warnings")

lazy val startupTransition: State => State = "conventionalCommits" :: _
Global / onLoad := startupTransition compose (Global / onLoad).value

val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest-propspec" % "3.2.14" % "test",
    "org.scalatestplus" %% "scalacheck-1-16" % "3.2.14.0" % "test",
    "eu.timepit" %% "refined" % "0.10.1",
    "org.typelevel" %% "cats-core" % "2.8.0",
    "org.typelevel" %% "cats-mtl" % "1.3.0",
    "org.typelevel" %% "cats-effect" % "3.3.12",
    "org.typelevel" %% "shapeless3-deriving" % "3.2.0",
    "org.http4s" %% "http4s-blaze-server" % "0.23.12",
    "org.http4s" %% "http4s-circe" % "0.23.16",
    "org.http4s" %% "http4s-dsl" % "0.23.16",
    "org.http4s" %% "http4s-blaze-client" % "0.23.12",
    "io.circe" %% "circe-generic" % "0.14.3",
    "io.circe" %% "circe-core" % "0.14.3",
    "io.circe" %% "circe-generic" % "0.14.3",
    "io.circe" %% "circe-parser" % "0.14.3",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.0.6",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.0.6",
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.0.6",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-client" % "1.0.6",
    "org.typelevel" %% "log4cats-core" % "2.5.0",
    "org.typelevel" %% "log4cats-slf4j" % "2.5.0",
    "org.slf4j" % "slf4j-api" % "2.0.3",
    "org.slf4j" % "slf4j-simple" % "2.0.3",
  ),
)

val commonDockerSettings = Seq(
  dockerBaseImage := "eclipse-temurin:17.0.4_8-jre",
  dockerEnvVars := Map("PORT" -> "8080", "HOST" -> "0.0.0.0"),
  Docker / packageName := packageName.value,
  Docker / version := version.value,
)

addCommandAlias("ubidocGenerate", "clean; unidoc; ubidoc; clean; unidoc")
addCommandAlias("qaCheck", "scalafmtCheckAll; scalafixAll --check; wartremoverInspect")

lazy val root = project
  .in(file("."))
  .settings(
    name := "mdm",
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    sonatypeProfileName := "dev.atedeg",
    jacocoAggregateReportSettings := JacocoReportSettings(
      title = "mdm coverage report",
      formats = Seq(JacocoReportFormats.XML),
    ),
    publish / skip := true,
  )
  .aggregate(
    stocking,
    production,
    `milk-planning`,
    `production-planning`,
    `products-shared-kernel`,
    restocking,
    `client-orders`,
    pricing,
    `alerts-manager`,
  )

lazy val utils = project
  .in(file("utils"))
  .settings(commonSettings)
  .settings(
    publish / skip := true,
  )

lazy val `milk-planning` = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("milk-planning"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(utils, `products-shared-kernel`)

lazy val production = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("production"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(utils, `products-shared-kernel`)

lazy val `products-shared-kernel` = project
  .in(file("products-shared-kernel"))
  .settings(commonSettings)
  .settings(
    publish / skip := true,
  )
  .dependsOn(utils)

lazy val stocking = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("stocking"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(utils, `products-shared-kernel`)

lazy val `client-orders` = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("client-orders"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(utils, `products-shared-kernel`)

lazy val restocking = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("restocking"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(utils, `products-shared-kernel`)

lazy val `production-planning` = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("production-planning"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(utils, `products-shared-kernel`)

lazy val pricing = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("pricing"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    dockerExposedPorts := Seq(8080),
  )
  .dependsOn(utils, `products-shared-kernel`)

lazy val `alerts-manager` = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("alerts-manager"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .settings(
    libraryDependencies ++= Seq("org.eclipse.ditto" % "ditto-client" % "3.0.0"),
  )
  .dependsOn(utils)

lazy val maintenance = project
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .in(file("maintenance"))
  .settings(commonSettings)
  .settings(commonDockerSettings)
  .dependsOn(utils)
