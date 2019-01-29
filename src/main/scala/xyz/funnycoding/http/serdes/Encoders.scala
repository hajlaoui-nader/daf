package xyz.funnycoding.http.serdes

import cats.Applicative
import io.circe.Encoder
import io.circe.generic.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe._
import xyz.funnycoding.data._
import xyz.funnycoding.persistence.VisitorsAlg.InsertionError
import xyz.funnycoding.http.{JsonError, JsonSuccess}


object Encoders extends JsonEncoders with Encoders

trait Encoders {

  /**
    * The manually-written implicits are so that we don't have a circular implicit resolutin on EntityEncoder..
    */
  implicit def entityEncoderFromJsonEncoder[F[_], A: Encoder](
      implicit fApplicative: Applicative[F],
      aEncoder: Encoder[A]): EntityEncoder[F, A] = {
    jsonEncoderOf[F, A](fApplicative, aEncoder)
  }


}

trait JsonEncoders {

  implicit val visitorIdJsonEncoder: Encoder[VisitorId] = Encoder.encodeLong.contramap(_.value)

  implicit val visitorJsonEncoder: Encoder[Visitor] = deriveEncoder[Visitor]

  implicit val newVisitorJsonEncoder: Encoder[NewVisitor] = deriveEncoder[NewVisitor]

  implicit val jsonErrorJsonEncoder: Encoder[JsonError] = deriveEncoder[JsonError]

  implicit val jsonSuccessJsonEncoder: Encoder[JsonSuccess] = deriveEncoder[JsonSuccess]

  implicit val visitorInsertionErrorJsonEncoder: Encoder[InsertionError] =
    Encoder[JsonError].contramap {
      case InsertionError(t) =>
        val msg = Option(t.getMessage)
          .map(s => s"Could not insert Visitor due to: $s")
          .getOrElse("Could not insert Visitor")
        JsonError(msg)
    }

}
