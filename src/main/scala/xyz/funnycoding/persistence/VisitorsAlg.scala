package xyz.funnycoding.persistence

import cats.effect.Effect
import cats.implicits._
import xyz.funnycoding.data._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import scala.util.control.NonFatal

object VisitorsAlg {

  /**
    * Given a transactor, returns a Doobie-based implementation of Tweets algebra
    */
  def doobie[F[_]: Effect](xa: Transactor[F]): DoobieVisitorsAlg[F] = new DoobieVisitorsAlg[F](xa)

  sealed trait Error
  final case class InsertionError(underlying: Throwable) extends Error
}

abstract class VisitorsAlg[F[_]: Effect] {

  import xyz.funnycoding.persistence.VisitorsAlg.InsertionError

  def insert(newVisitor: NewVisitor): F[Either[InsertionError, Visitor]]
  def listVisitors: F[Seq[Visitor]]
}

object DoobieVisitorsAlg {

  implicit val tweetIdMeta: Meta[VisitorId] = Meta.LongMeta.xmap(VisitorId.apply, _.value)

  private[persistence] def insertNewVisitor(newVisitor: NewVisitor): Update0 = {
    sql"""
       INSERT
       INTO visitors ("name")
       VALUES (${newVisitor})
       """.update
  }

  private[persistence] val listAllVisitors: Query0[Visitor] = {
    sql"""
      SELECT v.id, v.name
      FROM visitors as v
    """.query[Visitor]
  }
}

class DoobieVisitorsAlg[F[_]: Effect](xa: Transactor[F]) extends VisitorsAlg[F] {
  import VisitorsAlg._
  import DoobieVisitorsAlg._

  override def insert(newVisitor: NewVisitor): F[Either[InsertionError, Visitor]] = {
    insertNewVisitor(newVisitor)
      .withUniqueGeneratedKeys[VisitorId]("id")
      .transact(xa)
      .map(id => Either.right[InsertionError, Visitor](Visitor(id, newVisitor.name)))
      .recover {
        case NonFatal(e) => Either.left[InsertionError, Visitor](InsertionError(e))
      }
  }


  override def listVisitors: F[Seq[Visitor]] = listAllVisitors.to[List].transact(xa).map(_.toSeq)
}
