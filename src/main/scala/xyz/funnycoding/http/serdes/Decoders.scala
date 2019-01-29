package xyz.funnycoding.http.serdes

import cats.effect.Effect
import io.circe.Decoder
import io.circe.generic.semiauto._
import org.http4s.EntityDecoder
import org.http4s.circe._
import xyz.funnycoding.data._
import org.http4s.InvalidMessageBodyFailure

object Decoders extends JsonDecoders with Decoders

trait Decoders {
  implicit def entityDecoderFromJsonDecoder[F[_]: Effect, A: Decoder]: EntityDecoder[F, A] =
    jsonOf[F, A].bimap(f => InvalidMessageBodyFailure(f.getMessage()), identity)
}

trait JsonDecoders {
  implicit val visitorIdJsonDecoder: Decoder[VisitorId] =
    Decoder.decodeLong.map(VisitorId.apply)

  implicit val visitorJsonDecoder: Decoder[Visitor] = deriveDecoder[Visitor]

  implicit val newVisitorJsonDecoder: Decoder[NewVisitor] = deriveDecoder[NewVisitor]

}
