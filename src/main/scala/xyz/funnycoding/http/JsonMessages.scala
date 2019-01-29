package xyz.funnycoding.http

final case class JsonError(message: String)           extends AnyVal
final case class JsonSuccess(message: Option[String]) extends AnyVal
