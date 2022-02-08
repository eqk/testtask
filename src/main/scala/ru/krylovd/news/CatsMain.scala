package ru.krylovd.news

import cats.effect._
import org.typelevel.log4cats.slf4j.Slf4jLogger

object CatsMain extends Tasks with IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      logger <- Slf4jLogger.create[IO]
      _ <- logger.info("START")
      res <- tasks[IO].use {
        case (server, scraper) => IO.race(server, scraper).map(_.merge)
      }
    } yield res

}
