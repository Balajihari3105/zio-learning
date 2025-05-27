import zio.Console.*
import zio.*

object MainApp extends ZIOAppDefault {

  // The run method is where you define your application's main logic
  def run = for {
    ret <- retrySchedule(10)
    _ <- ZIO.logInfo(s"the $ret")
    _ <- ZIO.logInfo("Hello from Main App!") // Print a message to the console
  } yield ()


  val retrying = Schedule.spaced(10.seconds) && Schedule.recurs(4)
  def retrySchedule(s: Int)={
    for{
      _ <- ZIO.logInfo(s"the int $s")
      t <- if ((s-1) %2==0) ZIO.succeed(s) else ZIO.logInfo(s"falied retrying") *> ZIO.fail(new Exception(s"the failed"))
    } yield t
  }.retry(retrying)
}