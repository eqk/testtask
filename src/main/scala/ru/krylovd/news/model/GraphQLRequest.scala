package ru.krylovd.news.model

import io.circe.JsonObject

case class GraphQLRequest(
    query: String,
    operationName: Option[String],
    variables: JsonObject
)
