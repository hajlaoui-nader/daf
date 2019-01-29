package server

import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.blaze._
import xyz.funnycoding.config._
import xyz.funnycoding.http.service._
import org.http4s.server.Router

object Server extends IOApp {

  import xyz.funnycoding.persistence.{HikariOps, Migration}

  def run(args: List[String]): IO[ExitCode] =
    for {
      appConfigEither <- IO(AppConf.load())
      appConfig = appConfigEither match {
        case Right(c) => c
        case Left(err) =>
          throw new IllegalStateException(s"Could not load AppConfig: ${err.toList.mkString("\n")}")
      }
      dbConfig = appConfig.db
      _  <- Migration.withConfig(dbConfig)
      xa <- HikariOps.toTransactor(dbConfig)
    } yield {
      val value: IO[ExitCode] = BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(Router("/visitors" -> Visitors.service(xa)).orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)

      value.unsafeRunSync

    }
}
