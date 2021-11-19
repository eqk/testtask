package ru.krylovd.news.http

import cats.effect.Async
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import ru.krylovd.news.algebra.GraphQL
import ru.krylovd.news.model.GraphQLRequest

object GraphQLRoutes {

  def apply[F[_]: Async](graphQL: GraphQL[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    HttpRoutes.of[F] {
      case req @ POST -> Root / "graphql" =>
        req.as[GraphQLRequest].flatMap(graphQL.query).flatMap {
          case Right(json) => Ok(json)
          case Left(json)  => BadRequest(json)
        }
    }
  }

}
