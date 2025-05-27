/*
import zio.*
import zio.Console.*

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ZioWithRunTime extends ZIOAppDefault {

  case class LogConfig(level: String)

  trait Logger {
    def log(message: String): UIO[Unit]
  }

  object Logger {
    def live(config: LogConfig): Logger = new Logger {
      override def log(message: String): UIO[Unit] = {
        if (config.level == "INFO" || config.level == "ERROR") {
          ZIO.succeed(println(s"[${config.level}] $message"))
        } else {
          ZIO.succeed(println(""))
        }
      }
    }

    def log(message: String): URIO[Logger, Unit] = ZIO.service[Logger].flatMap(_.log(message))

    def liveLayer(config: LogConfig): ZLayer[Any, Nothing, Logger] =
      ZLayer.succeed(live(config))
  }

  val appLogic: URIO[Logger, Unit] = for {
    _ <- Logger.log("Application started")
    _ <- Logger.log("Processing data...")
    _ <- Logger.log("Application finished")
  } yield ()

  // Create a program that provides the Logger environment
  def run: ZIO[Any, Throwable, Unit] = {
    schedule
  }

  def schedule: ZIO[Any, Throwable, Unit] = {
    val logConfig = LogConfig("INFO") // Change to "DEBUG" to see more logs

    // Provide the Logger environment
    val loggerLayer = Logger.liveLayer(logConfig)

    // Define the job to run repeatedly
    val job = ZIO.attempt(println("The job is running"))

    // Create a schedule that runs every 3 seconds
    val recure = Schedule.fixed(2.seconds)

    // Combine appLogic and the repeated job
    for {
      _ <- appLogic.provideLayer(loggerLayer).fork // Run appLogic once
      _ <- job.repeat(recure).fork // Then start the repeated job
      _ <- fun.fork
      _<- ZIO.never
    } yield ()
    // Provide the logger layer to the combined effect
  }

  def fun= ZIO.attempt(println("the function exceution"))

}

*/
