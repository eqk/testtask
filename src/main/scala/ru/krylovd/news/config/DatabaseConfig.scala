package ru.krylovd.news.config

import cats.implicits._
import ciris._

case class DatabaseConfig(
    url: String,
    user: String,
    password: String,
    poolSize: Int
)

object DatabaseConfig {
  val defaultJdbcUrl = "jdbc:postgresql://127.0.0.1:5432/postgres?currentSchema=public"
  def parser[F[_]]: ConfigValue[F, DatabaseConfig] =
    (
      env("DB_URL").as[String].default(defaultJdbcUrl),
      env("DB_USER").as[String].default("postgres"),
      env("DB_PASSWORD").as[String].default("pgpass"),
      env("DB_POOL_SIZE").as[Int].default(10)
    ).parMapN(DatabaseConfig.apply)
}
