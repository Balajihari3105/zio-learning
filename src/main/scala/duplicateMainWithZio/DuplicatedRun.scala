package zio

import play.api.http.HttpChunk.Chunk
import zio.{ZIO, ZIOApp, ZIOAppDefault}
import zio.ZIOAppPlatformSpecific
import zio._

object DuplicatedRun extends App {
 App.run.exitCode
}

object App {
  // Define the main ZIO effect
  def run: ZIO[Console, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Printing the ZIO") // Log an info message
  } yield ()
}
