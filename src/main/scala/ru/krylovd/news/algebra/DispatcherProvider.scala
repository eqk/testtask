package ru.krylovd.news.algebra

import cats.effect.std.Dispatcher

trait DispatcherProvider[F[_]] {
  val dispatcher: Dispatcher[F]
}

object DispatcherProvider {
  def apply[F[_]](implicit dispatcherProvider: DispatcherProvider[F]): DispatcherProvider[F] =
    dispatcherProvider

  def apply[F[_]](dsp: Dispatcher[F]): DispatcherProvider[F] =
    new DispatcherProvider[F] {
      val dispatcher: Dispatcher[F] = dsp
    }
}
