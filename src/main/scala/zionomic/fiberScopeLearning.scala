package zionomic

import akka.util.ByteString
import zio.{Schedule, Scope, Task, UIO, ZIO, ZIOAppDefault, durationInt}

import java.util.UUID

object fiberScopeLearning extends ZIOAppDefault {

  def fibScp(scope: Scope) = {
    (finDemon().forkIn(scope) *> ZIO.never).onInterrupt {
      ZIO.debug("Interrupted, restarting...")
    }
  }

  def finDemon() = {
    ZIO.debug(s"the deubg is on..")
  }.schedule(Schedule.fixed(1.second))

  import zio._

  def effect = ZIO.attempt {
    var i = 0
    while (true) {
      //      Thread.sleep(1000) // Blocking call, interruptible
      println(s"Count: $i")
      i += 1
    }
  }.interruptible


  def attempBlockingInterput = ZIO.attemptBlockingInterrupt {
    var i = 0
    while (i < 100000) {
      Thread.sleep(1000) // Blocking call, interruptible
      println(s"Count: $i")
      i += 1
    }
  }.onInterrupt(ZIO.logInfo("Effect interrupted")) // Log when interrupted


  def run = for {
    _ <- ZIO.logInfo("Starting effect")
    fiber <- effect.fork
    _ <- ZIO.sleep(5.seconds) // Let it run for 5 seconds
    _ <- ZIO.logInfo("Interrupting fiber")
    _ <- fiber.interrupt // Interrupt the fiber
    _ <- ZIO.logInfo("Program completed")
  } yield ()
  /* for {
     _ <- for {
       outer <- ZIO.scope
       f <- fibScp(outer).provide(Scope.default).forkIn(outer)
       _ <- ZIO.sleep(4.seconds)
       _ <- f.interrupt
       _ <- ZIO.never
     } yield ()
     _ <- ZIO.sleep(5.seconds)
     _ <- ZIO.debug("Closing the outermost scope.")
   } yield ()*/
}


import zio._
import java.util.concurrent.atomic.AtomicBoolean

object CancelableExample extends ZIOAppDefault {

  def effect(ref: AtomicBoolean): ZIO[Any, Throwable, Unit] =
    ZIO.attemptBlockingCancelable {
      var i = 0
      while (i < 100000 && !ref.get()) { // Check cancellation flag
        Thread.sleep(1000)
        println(s"Count: $i")
        i += 1
      }
    }(ZIO.succeed(ref.set(true))) // Cancel sets flag to true

  def run = for {
    _ <- ZIO.logInfo("Starting effect")
    ref <- ZIO.succeed(AtomicBoolean(false))
    fiber <- effect(ref).fork
    _ <- ZIO.sleep(5.seconds)
    _ <- ZIO.logInfo("Interrupting fiber")
    _ <- fiber.interrupt
    _ <- ZIO.logInfo("Program completed")
  } yield ()
}

import zio._
import zio.stream._
import zio.Console._

object DrainExample extends ZIOAppDefault {

  // A stream that generates random numbers and prints them as a side effect
  val randomStream =
    ZStream.repeatZIO {
      for {
        nextInt <- Random.nextIntBounded(100) // Generate a random number between 0 and 99
        _ <- printLine(s"Generated number: $nextInt") // Side effect: print the number
      } yield nextInt
    }.take(5) // Limit to 5 elements

  // Using drain to execute the stream's effects without emitting values
  val drainedStream =
    randomStream.drain


  val interubale = ZIO.attemptBlockingInterrupt(ZStream.succeed(19))

  // The program: run the drained stream
  override def run =
    drainedStream.runDrain // runDrain fully executes the stream and discards all output

}


object StreamController extends ZIOAppDefault {

  def genRandomUUid() = UUID.randomUUID()

  val applogic = (for {
    record <- ZStream.succeed(genRandomUUid())
      .tap(s => ZIO.debug(s"the generated UUid $s"))
      .mapZIOPar(2) {
        message =>
          for {
            task1 <- proceessTask1(message).zipPar(processTask2(message))
          } yield message
      }
  } yield ()).repeat(Schedule.spaced(5.millisecond))

  def proceessTask1(value: UUID): ZIO[Any, Nothing, Unit] = {
    ZIO.debug(s"the task 1 process $value").delay(1.second)
  }

  def processTask2(uuid: UUID): ZIO[Any, Nothing, Unit] = {
    ZIO.debug(s"the task 2 process $uuid").delay(2.second)
  }


  override def run =
    for {
      s <- applogic.runDrain
      _ <- ZIO.never
    } yield ()
}

import play.api.libs.json._

object PlayJsonExample extends App {
  // Case class
  case class Response(id: Option[String])

  // JSON Reads
  implicit val responseReads: Reads[Response] =
    (__ \ "data" \ 0 \ "id").readNullable[String].map(id => Response(id))

  // JSON string
  val jsonString =
    """
    {
      "details": {
        "name": "balaji"
      },
      "data": [
        {
          "id": "1234"
        }
      ]
    }
  """

  // Parse JSON


  val byterString = Json.toJson(jsonString)
  println(s"the toJson $byterString")

  val json = Json.parse(jsonString)

  println(s"the parse $json"
  )
  val result = json.validate[Response]

  // Handle result
  result match {
    case JsSuccess(response, _) => println(s"Parsed: $response")
    case JsError(errors) => println(s"Errors: $errors")
  }
}


import zio._

object InterruptExample extends ZIOAppDefault {
  def effect: ZIO[Any, Nothing, Unit] = {
    def step(i: Int): ZIO[Any, Nothing, Unit] =
      for {
        _ <- ZIO.succeed(println(s"Count: $i")) // Print count
        _ <- ZIO.sleep(1.seconds)
        _ <- ZIO.yieldNow // Yield to allow interruption checks
        _ <- step(i + 1) // Recurse infinitely
      } yield ()

    step(0).interruptible // Explicitly interruptible
  }

  def run = for {
    _ <- ZIO.logInfo("Starting effect")
    fiber <- effect.fork
    _ <- ZIO.sleep(5.seconds)
    _ <- ZIO.logInfo("Interrupting fiber")
    _ <- fiber.interrupt
    _ <- ZIO.logInfo("Program completed")
  } yield ()
}

import zio._
import zio._
import zio.Console._

object InterruptibleExample extends ZIOAppDefault {


  def attempBlockingInterput = ZIO.attempt /*ZIO.attemptBlockingInterrupt */ {
    var i = 0
    while (i < 100000) {
      Thread.sleep(1000) // Blocking call, interruptible
      println(s"Count: $i")
      i += 1
    }
  }.interruptible /*.onInterrupt(ZIO.logInfo("Effect interrupted"))*/
  // Log when interrupted

  //
  // Simulate playing a harmonica note for 5 seconds
  val playNote =
    ZIO.debug(s"Finished playing note!").delay(5.seconds).uninterruptible


  // Program: Start playing, interrupt after 2 seconds
  val program = for {
    fiber <- playNote.fork
    _ <- ZIO.sleep(2.seconds) *> fiber.interrupt
    _ <- ZIO.debug("Interrupted the note!")

    blockingFiber <- attempBlockingInterput.fork
    _ <- blockingFiber.interrupt
  } yield ()

  def run = program
}


object DisconnectExample extends ZIOAppDefault {
  val a: UIO[Unit] =
    ZIO.never.ensuring(
      ZIO.debug("Closed A").delay(3.seconds)
    )
  val b: UIO[Unit] =
    ZIO.never.ensuring(ZIO.debug("Closed B").delay(5.seconds)
    ).disconnect

  def run = (for {
    fiber <- (a <&> b).fork
    _
      <- Clock.sleep(1.second)
    _
      <- fiber.interrupt
    _
      <- ZIO.debug("Done interrupting")
  } yield ()) *> ZIO.debug("Scope closed interrupting..")
}