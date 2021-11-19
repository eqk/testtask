package ru.krylovd.news.config

import scala.concurrent.duration.{DurationInt, FiniteDuration}

import cats.implicits._
import ciris._

case class ScrapeConfig(nytimesUrl: String, interval: FiniteDuration)

object ScrapeConfig {
  val defaultUrl = "https://nytimes.com"
  def parser[F[_]]: ConfigValue[F, ScrapeConfig] =
    (
      env("NYT_URL").as[String].default(defaultUrl),
      env("SCRAPE_INTERVAL").as[FiniteDuration].default(1.hour)
    ).parMapN(ScrapeConfig.apply)
}