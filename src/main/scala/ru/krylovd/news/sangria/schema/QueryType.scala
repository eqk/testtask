package ru.krylovd.news.sangria.schema

import cats.effect._
import ru.krylovd.news.algebra.DispatcherProvider
import ru.krylovd.news.repo.NewsRepo
import sangria.schema._

object QueryType {

  def apply[F[_]: Async: DispatcherProvider]: ObjectType[NewsRepo[F], Unit] =
    ObjectType(
      name = "Query",
      fields = fields(
        Field(
          name = "news",
          fieldType = ListType(NewsType[F]),
          description = Some("All news"),
          resolve = c => DispatcherProvider[F].dispatcher.unsafeToFuture(c.ctx.all)
        )
      )
    )

  def schema[F[_]: Async: DispatcherProvider]: Schema[NewsRepo[F], Unit] =
    Schema(QueryType[F])

}
