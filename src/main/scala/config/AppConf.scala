package config

import com.typesafe.config._
import pureconfig.error.ConfigReaderFailures
import pureconfig.syntax._
import pureconfig.module.enumeratum._
import pureconfig.generic.auto._

final case class AppConf(server: ServerConf, db: DBConf, swagger: SwaggerConf)

object AppConf {
  def load(): Either[ConfigReaderFailures, AppConf] = ConfigFactory.load.to[AppConf]
}
