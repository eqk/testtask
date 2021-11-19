package ru.krylovd.news.config

import cats.implicits._
import ciris.ConfigValue

final case class AppConfig(db: DatabaseConfig, scrape: ScrapeConfig)

object AppConfig {
  def parser[F[_]]: ConfigValue[F, AppConfig] =
    (
      DatabaseConfig.parser,
      ScrapeConfig.parser
    ).parMapN(AppConfig.apply)
}
