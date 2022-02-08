package ru.krylovd.news.sangria

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

import _root_.sangria.ast._
import _root_.sangria.execution.{WithViolations, _}
import _root_.sangria.marshalling.circe._
import _root_.sangria.parser.{QueryParser, SyntaxError}
import _root_.sangria.schema._
import _root_.sangria.validation._
import cats.effect._
import cats.syntax.all._
import io.circe.{Json, JsonObject}
import ru.krylovd.news.algebra.GraphQL
import ru.krylovd.news.model.GraphQLRequest

object SangriaGraphQL {

  private def formatSyntaxError(e: SyntaxError): Json =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "message" -> Json.fromString(e.getMessage),
          "locations" -> Json.arr(
            Json.obj(
              "line" -> Json.fromInt(e.originalError.position.line),
              "column" -> Json.fromInt(e.originalError.position.column)
            )
          )
        )
      )
    )

  private def formatWithViolations(e: WithViolations): Json =
    Json.obj("errors" -> Json.fromValues(e.violations.map {
      case v: AstNodeViolation =>
        Json.obj(
          "message" -> Json.fromString(v.errorMessage),
          "locations" -> Json.fromValues(
            v.locations.map(loc =>
              Json.obj(
                "line" -> Json.fromInt(loc.line),
                "column" -> Json.fromInt(loc.column)
              )
            )
          )
        )
      case v => Json.obj("message" -> Json.fromString(v.errorMessage))
    }))

  private def formatThrowable(e: Throwable): Json =
    Json.obj(
      "errors" -> Json.arr(
        Json.obj(
          "class" -> Json.fromString(e.getClass.getName),
          "message" -> Json.fromString(e.getMessage)
        )
      )
    )

  def apply[F[_]: Async, A](
      schema: Schema[A, Unit],
      userContext: F[A]
  ): GraphQL[F] =
    new GraphQL[F] {

      def query(request: GraphQLRequest): F[Either[Json, Json]] =
        QueryParser.parse(request.query) match {
          case Success(ast) =>
            execute(schema, userContext, ast, request.operationName, request.variables)
          case Failure(e @ SyntaxError(_, _, _)) => fail(formatSyntaxError(e))
          case Failure(e)                        => fail(formatThrowable(e))
        }

      def fail(j: Json): F[Either[Json, Json]] = Async[F].pure(j.asLeft)

      def execute(
          schema: Schema[A, Unit],
          userContext: F[A],
          query: Document,
          operationName: Option[String],
          variables: JsonObject
      ): F[Either[Json, Json]] =
        for {
          ctx <- userContext
          implicit0(ec: ExecutionContext) <- Async[F].executionContext
          result <-
            Async[F]
              .fromFuture(
                Executor
                  .execute(
                    schema = schema,
                    queryAst = query,
                    userContext = ctx,
                    variables = Json.fromJsonObject(variables),
                    operationName = operationName,
                    exceptionHandler = ExceptionHandler {
                      case (_, e) ⇒ HandledException(e.getMessage)
                    }
                  )
                  .pure
              )
              .attempt
        } yield result match {
          case Right(json)               => json.asRight
          case Left(err: WithViolations) => formatWithViolations(err).asLeft
          case Left(err)                 => formatThrowable(err).asLeft
        }

    }

}
