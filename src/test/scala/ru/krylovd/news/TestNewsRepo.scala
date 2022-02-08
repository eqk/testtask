package ru.krylovd.news

import cats.Applicative
import ru.krylovd.news.model.Headline
import ru.krylovd.news.repo.NewsRepo

class TestNewsRepo[F[_]: Applicative] extends NewsRepo[F] {
  def all: F[List[Headline]] =
    Applicative[F].pure(
      List(
        Headline("title1", "link1"),
        Headline("title2", "link2"),
        Headline("title3", "link3")
      )
    )

  def update(headlines: List[Headline]): F[Unit] = Applicative[F].unit
}
