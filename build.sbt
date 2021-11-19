lazy val Http4sVersion = "0.23.6"
lazy val CirceVersion = "0.14.1"
lazy val Slf4jVersion = "1.7.32"
lazy val Log4JVersion = "2.14.0"
lazy val JacksonVersion = "2.13.0"
lazy val SangriaVersion = "2.1.6"
lazy val SangriaCirceVersion = "1.3.2"
lazy val Log4CatsSlf4jVersion = "2.1.1"
lazy val DoobieVersion = "1.0.0-RC1"
lazy val CatsEffectVersion = "3.2.9"
lazy val ScalaScraperVersion = "2.2.0"
lazy val CirisVersion = "2.2.1"
lazy val ZIOVersion = "1.0.12"
lazy val ZIOCatsVersion = "3.1.1.0"
lazy val ScalaTestVersion = "3.2.10"
lazy val ScalaTestCheckVersion = "3.2.10.0"
lazy val TestContainersVersion = "0.27.0"
lazy val TestContainersPostgresVersion = "1.16.2"

lazy val root = (project in file("."))
  .settings(
    organization := "ru.krylovd",
    name := "news",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,      
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "com.softwaremill.sttp.client" %% "http4s-backend" % "2.2.5",
      "com.softwaremill.sttp.client" %% "circe" % "2.2.5",
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
      "io.circe" %% "circe-optics" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion % Test,
      "org.sangria-graphql" %% "sangria" % SangriaVersion,
      "org.sangria-graphql" %% "sangria-circe" % SangriaCirceVersion,
      "net.ruippeixotog" %% "scala-scraper" % ScalaScraperVersion,
      "is.cir" %% "ciris" % CirisVersion,
      "org.tpolecat" %% "doobie-quill" % DoobieVersion,
      "dev.zio" %% "zio" % ZIOVersion,
      "dev.zio" %% "zio-interop-cats" % ZIOCatsVersion,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-15" % ScalaTestCheckVersion % Test,
      "com.dimafeng" %% "testcontainers-scala" % TestContainersVersion % Test,
      "org.testcontainers" % "postgresql" % TestContainersPostgresVersion % Test
    ) ++ loggingDeps,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )

lazy val loggingDeps = Seq(
  "org.slf4j" % "slf4j-api" % Slf4jVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % Log4JVersion,
  "org.apache.logging.log4j" % "log4j-core" % Log4JVersion,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % JacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
  "org.typelevel" %% "log4cats-slf4j" % Log4CatsSlf4jVersion,
  "org.typelevel" %% "log4cats-noop" % Log4CatsSlf4jVersion
)

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / watchBeforeCommand := Watch.clearScreen

