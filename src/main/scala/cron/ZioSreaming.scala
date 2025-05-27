package cron

import org.apache.pekko.actor.ActorSystem
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import zio.{Console, ZIOAppDefault, *}
import zio.stream.ZStream

object ZioStreaming extends ZIOAppDefault {

  def run = {
    val list = Seq(1, 2, 3, 4, 45)
    ZStream.fromIterator(list.iterator)
      .map(x => ZIO.attempt(throw new Exception(s"exception on $x"))) // Throwing an exception for demonstration
      .tapError(err => ZIO.logError(err.getMessage)) // Log the error
      .foreach(_ => Console.printLine("Processed successfully")) // Print success message
      .catchAll(_ => Console.printLine("An error occurred during processing")) // Handle any remaining error
    val result = calculation(0) // Call the calculation function

    result
      .tapError(err => ZIO.logError(err.getMessage)) // Log the error if it occurs
      .flatMap {
        case None =>
          ZIO.logInfo("the now expected") *> ZIO.succeed("Hello")
        case Some(v) => ZIO.succeed(v.toString)
      }
      .forEachZIO(s => Console.printLine(s))

    /*getSupplier.map(s => Console.printLine(s"$s"))
        getSupplier
          .tapError(err => ZIO.logError(err.toString))
          .flatMap {
            case Right(value) => ZIO.succeed(Seq("hello"))
            case Left(value) =>
              ZIO.logWarning("the warning") *> ZIO.succeed(Seq.empty[String])
          }
          .flatMap(seq => ZIO.foreach(seq)(s => ZIO.logInfo(s"$s")))*/
  }

  def getSupplier = {
    implicit val mat: ActorSystem = ActorSystem("proor")
    val client: StandaloneWSClient = StandaloneAhcWSClient()
    val req = ZIO.fromFuture(ec => client.url("https://www.google.com/").get())
    req.flatMap(res => {
      res match
        case e if Status.isSuccessful(e.status) => ZIO.attempt(Json.parse(res.body)).map(Right(_)).catchAll(err => ZIO.succeed(Left(err.getMessage)))
        case e => ZIO.left(e.body)
    })
  }

  def calculation(n: Int) = ZIO.attempt {
    if (n == 0)
      throw new RuntimeException("Calculation failed: n cannot be zero") // Simulate an error
    else if (n % 2 == 0) Some(n + 3)
    else None

  }

}