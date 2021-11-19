package ru.krylovd.news.sangria.schema

import ru.krylovd.news.model.Headline
import ru.krylovd.news.repo.NewsRepo
import sangria.schema._

object NewsType {

  def apply[F[_]]: ObjectType[NewsRepo[F], Headline] =
    ObjectType(
      name = "News",
      fieldsFn = () =>
        fields(
          Field(
            name = "title",
            fieldType = StringType,
            resolve = _.value.title,
            description = Some("News title")
          ),
          Field(
            name = "link",
            fieldType = StringType,
            resolve = _.value.link,
            description = Some("News link")
          )
        )
    )

}
