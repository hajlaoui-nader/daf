package xyz.funnycoding.http.service

import cats.effect.IO
import doobie.util.transactor.Transactor
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import xyz.funnycoding.persistence.VisitorsAlg
import xyz.funnycoding.data.NewVisitor
import org.http4s.EntityDecoder

object Visitors {
  import xyz.funnycoding.http.serdes.Decoders._
  import xyz.funnycoding.http.serdes.Encoders._

  def service(xa: Transactor[IO]): HttpRoutes[IO] = {
    val dao = VisitorsAlg.doobie[IO](xa)
    HttpRoutes.of[IO] {

      case GET -> Root  =>
      Ok(dao.listVisitors)

      case req @ POST -> Root => {
        req.decodeWith(EntityDecoder[IO, NewVisitor], strict = true) { newVisitor: NewVisitor =>
          dao.insert(newVisitor).flatMap {
            case Right(visitor)    => Created(visitor)
            case Left(insertErr) => BadRequest(insertErr)
          }
        }
      }
    }
  }

}
