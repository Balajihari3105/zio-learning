import com.typesafe.config.ConfigFactory
import zio.*
import zio.Config.{int, string}
import zio.config.magnolia.deriveConfig

import java.io.File
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class AppConf(myapp: MyAppConfig)

case class MyAppConfig(database: DataBase, server: Server)

case class DataBase(url: String, username: String, password: String)

case class Server(port: Long)


object MainApp1 extends ZIOAppDefault {

  override def run = {
    val appLogic = for {
      config <- ConfigModule.getConfig
      _ <- Console.printLine(s"Database URL: ${config.myapp.database.url}")
      _ <- Console.printLine(s"Server Port: ${config.myapp.server.port}")
    } yield ()

    appLogic.provideLayer(ConfigModule.live)
  }
}
import zio._
import zio.config._
import zio.config.typesafe._
import com.typesafe.config.ConfigFactory

object ConfigModule {
  type ConfigLayer =AppConf
  val live: ZLayer[Any, Config.Error, AppConf] = {
    val automaticDescription = deriveConfig[AppConf]
    val hoconSource: ConfigProvider = ConfigProvider.fromTypesafeConfig(ConfigFactory.load())

    val s = hoconSource.load(automaticDescription)
    ZLayer.fromZIO(s)
  }

  def getConfig :URIO[ConfigLayer, AppConf]=  ZIO.service[AppConf]
}


