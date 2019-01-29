package xyz.funnycoding.persistence

import org.scalatest.{FunSpec, Matchers}
import xyz.funnycoding.H2DatabaseService
import cats.effect.IO
import xyz.funnycoding.data._

class VisitorsAlgSpec  extends FunSpec with Matchers with H2DatabaseService {

  lazy val visitorsAlg = new DoobieVisitorsAlg[IO](H2Transactor)

  describe("insert") {
    it("should insert") {

      val value: IO[Unit] = for {
        init <- visitorsAlg.listVisitors
        _ = init shouldBe empty
        newVisitor <- visitorsAlg.insert(NewVisitor("Nader"))
        Right(inserted) = newVisitor
        listAfterInsert <- visitorsAlg.listVisitors
        _ = listAfterInsert should contain(inserted)
        getVisitor <- visitorsAlg.getVisitor(inserted.visitorId)
        Some(ins) = getVisitor
        _ = ins shouldBe inserted
      } yield ()
      value.unsafeRunSync()
    }
  }
}
