package xyz.funnycoding.data

final case class VisitorId(value: Long)   extends AnyVal
final case class NewVisitor(name: String) extends AnyVal
final case class Visitor(visitorId: VisitorId, name: String)
