lazy val CatsEffectVersion = "3.3.5"
lazy val CirceVersion = "0.14.1"
lazy val CirisVersion = "2.2.1"
lazy val DoobieVersion = "1.0.0-RC1"
lazy val Http4sVersion = "0.23.10"
lazy val JacksonVersion = "2.13.0"
lazy val Log4CatsSlf4jVersion = "2.1.1"
lazy val Log4JVersion = "2.14.1"
lazy val SangriaCirceVersion = "1.3.2"
lazy val SangriaVersion = "2.1.6"
lazy val ScalaScraperVersion = "2.2.0"
lazy val ScalaTestCheckVersion = "3.2.10.0"
lazy val ScalaTestVersion = "3.2.10"
lazy val Slf4jVersion = "1.7.32"
lazy val TestContainersPostgresVersion = "1.16.2"
lazy val TestContainersVersion = "0.27.0"
lazy val ZIOCatsVersion = "3.1.1.0"
lazy val ZIOVersion = "1.0.12"

addCommandAlias("f", ";scalafixAll;scalafmtAll")

def scalafixRunExplicitly: Def.Initialize[Task[Boolean]] =
  Def.task {
    executionRoots.value.exists { root =>
      Seq(
        scalafix.key,
        scalafixAll.key
      ).contains(root.key)
    }
  }

lazy val root = (project in file("."))
  .settings(
    organization := "ru.krylovd",
    name := "news",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "circe" % "3.4.1",
      "com.softwaremill.sttp.client3" %% "http4s-backend" % "3.4.1",
      "dev.zio" %% "zio" % ZIOVersion,
      "dev.zio" %% "zio-interop-cats" % ZIOCatsVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-optics" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion % Test,
      "is.cir" %% "ciris" % CirisVersion,
      "net.ruippeixotog" %% "scala-scraper" % ScalaScraperVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.sangria-graphql" %% "sangria" % SangriaVersion,
      "org.sangria-graphql" %% "sangria-circe" % SangriaCirceVersion,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-15" % ScalaTestCheckVersion % Test,
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-quill" % DoobieVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    ) ++ loggingDeps,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin(scalafixSemanticdb),
    ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0",
    Compile / scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-Wunused"
    ),
    scalacOptions --= {
      if (!scalafixRunExplicitly.value) Seq() else Seq("-Xfatal-warnings")
    },
    semanticdbEnabled := true, // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision, // use Scalafix compatible version
  )

lazy val loggingDeps = Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % JacksonVersion,
  "org.apache.logging.log4j" % "log4j-core" % Log4JVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % Log4JVersion,
  "org.typelevel" %% "log4cats-noop" % Log4CatsSlf4jVersion,
  "org.typelevel" %% "log4cats-slf4j" % Log4CatsSlf4jVersion,
  "org.slf4j" % "slf4j-api" % Slf4jVersion
)
