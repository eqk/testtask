package ru.krylovd.news.algebra

import io.circe.Json
import ru.krylovd.news.model.GraphQLRequest

trait GraphQL[F[_]] {
  def query(request: GraphQLRequest): F[Either[Json, Json]]
}
