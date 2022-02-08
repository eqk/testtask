package ru.krylovd.news.repo

import cats.effect.Sync
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.quill.DoobieContext
import io.getquill.{idiom => _, _}
import org.typelevel.log4cats.Logger
import ru.krylovd.news.model.Headline

trait NewsRepo[F[_]] {
  def all: F[List[Headline]]
  def update(headlines: List[Headline]): F[Unit]
}

object NewsRepo {

  def fromTransactor[F[_]: Sync: Logger](xa: Transactor[F]): NewsRepo[F] =
    new NewsRepo[F] {
      val dc = new DoobieContext.Postgres(Literal); import dc._

      def all: F[List[Headline]] = {
        val q = quote(query[Headline])
        run(q).transact(xa)
      }

      override def update(headlines: List[Headline]): F[Unit] = {
        val q = quote {
          liftQuery(headlines).foreach { headline =>
            query[Headline]
              .insert(headline)
              .onConflictUpdate(_.link)((t, e) => t.title -> e.title)
          }
        }

        run(q).transact(xa).void
      }
    }

}
