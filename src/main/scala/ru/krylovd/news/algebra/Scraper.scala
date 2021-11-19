package ru.krylovd.news.algebra

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import ru.krylovd.news.model.Headline

trait Scraper[F[_]] {
  def headlines(): F[List[Headline]]
}

object Scraper {
  def NYTimes[F[_]: Sync](
    url: String
  ): Scraper[F] = new Scraper[F] {
    val browser = JsoupBrowser()

    def headlines(): F[List[Headline]] =
      for {
        doc <- Sync[F].pure(browser.get(url))
        parsed <- Sync[F].delay(parseHeadlines(doc))
      } yield parsed

    def parseHeadlines(doc: Document): List[Headline] = {
      val headlineDocs = doc >> elementList("a:has(h3)")
      headlineDocs.map(el => Headline(el >> text("h3"), el >> attr("href")))
    }
  }
}