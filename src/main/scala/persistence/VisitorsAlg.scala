package persistence

import cats.effect.Effect
import data.NewVisitor
import doobie.implicits._

abstract class VisitorsAlg [F[_]: Effect] {
  def insertNewVisitor(newVisitor: NewVisitor): F[Either[Error, NewVisitor]]
}

object DoobieVisitorsAlg {

  import doobie.util.update.Update0

  private[persistence] def insertNewVisitor(newVisitor: NewVisitor): Update0 = {
    sql"""
       INSERT
       INTO visitors ("name")
       VALUES (${newVisitor})
       """.update
  }
}
