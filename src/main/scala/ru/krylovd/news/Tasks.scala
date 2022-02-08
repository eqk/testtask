package ru.krylovd.news

import _root_.sangria.schema._
import cats.effect._
import cats.effect.std.Dispatcher
import cats.implicits._
import doobie.hikari._
import doobie.util.ExecutionContexts
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import ru.krylovd.news.algebra.{DispatcherProvider, Scraper, ScraperService}
import ru.krylovd.news.config.{AppConfig, DatabaseConfig, ScrapeConfig}
import ru.krylovd.news.http.{GraphQLRoutes, PlaygroundRoutes}
import ru.krylovd.news.repo.NewsRepo
import ru.krylovd.news.sangria.schema.QueryType

trait Tasks {

  def transactor[F[_]: Async](
      config: DatabaseConfig
  ): Resource[F, HikariTransactor[F]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool[F](config.poolSize)
      txa <- HikariTransactor.newHikariTransactor[F](
        "org.postgresql.Driver",
        config.url,
        config.user,
        config.password,
        ec
      )
    } yield txa

  def server[F[_]: Async: DispatcherProvider](repo: NewsRepo[F]): Resource[F, Server] = {
    val gql = sangria.SangriaGraphQL(
      Schema(
        query = QueryType[F]
      ),
      repo.pure[F]
    )

    val routes = GraphQLRoutes[F](gql) <+> PlaygroundRoutes()

    BlazeServerBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(routes.orNotFound)
      .resource
  }

  def scrapper[F[_]: Async: Logger](
      repo: NewsRepo[F],
      cfg: ScrapeConfig
  ): F[Unit] =
    for {
      scrp <- Scraper.NYTimes[F](cfg.nytimesUrl).pure[F]
      _ <- ScraperService[F](scrp, repo, Some(cfg.interval)).scrape
    } yield ()

  def tasks[F[_]: Async]: Resource[F, (F[ExitCode], F[ExitCode])] = {
    val loadConfig = AppConfig.parser[F].load[F]

    for {
      implicit0(logger: SelfAwareStructuredLogger[F]) <- Resource.eval(Slf4jLogger.create[F])
      _ <- Resource.eval(Logger[F].info("START"))
      cfg <- Resource.liftK[F](loadConfig)
      xa <- transactor[F](cfg.db)
      repo = NewsRepo.fromTransactor(xa)
      implicit0(dispatcher: DispatcherProvider[F]) <- Dispatcher[F].map(DispatcherProvider(_))
      srv = server[F](repo)
        .use(_ =>
          Async[F].never
            .as(ExitCode.Error) <* logger.info("Server stopped")
        )
      scraper = scrapper[F](repo, cfg.scrape).as(ExitCode.Error)
    } yield (srv, scraper)
  }
}
