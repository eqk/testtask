package ru.krylovd.news

import zio._
import zio.interop.catz._
import cats.effect.{ExitCode => CatsExitCode}


object ZIOMain extends Tasks with CatsApp {

  def exitCodes(catsExitCode: CatsExitCode): ExitCode = catsExitCode match {
    case CatsExitCode.Success => ExitCode.success
    case CatsExitCode.Error => ExitCode.failure
    case CatsExitCode(code) => ExitCode(code)
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    tasks[Task].use {
      case (server, scraper) => Task.raceAll(server, List(scraper)).map(exitCodes)
    }.orDie
}