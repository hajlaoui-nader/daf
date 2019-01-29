package http.service

import cats.effect.IO
import doobie.util.transactor.Transactor
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object Visitors {

  def service(xa: Transactor[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name., $xa")
  }

}
