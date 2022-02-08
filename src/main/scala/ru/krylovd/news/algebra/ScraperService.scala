package ru.krylovd.news.algebra

import scala.concurrent.duration.FiniteDuration

import cats.effect.Temporal
import cats.effect.kernel.Async
import cats.implicits._
import org.typelevel.log4cats.Logger
import ru.krylovd.news.repo.NewsRepo

trait ScraperService[F[_]] {
  def scrape: F[Unit]
}

object ScraperService {

  def apply[F[_]: Async: Logger](
      scraper: Scraper[F],
      repo: NewsRepo[F],
      interval: Option[FiniteDuration]
  ): ScraperService[F] =
    new ScraperService[F] {

      def scrape: F[Unit] =
        for {
          headlines <- scraper.headlines()
          _ <- repo.update(headlines)
          _ <- interval match {
            case Some(i) => Temporal[F].sleep(i) *> scrape
            case None    => Logger[F].info("Scrapper stopped")
          }
        } yield ()
    }
}
