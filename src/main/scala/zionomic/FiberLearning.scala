package zionomic

import zio.{Schedule, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.durationInt
import zio.*
import zio.Console.printLine
import zio.http.URL
import zionomic.asyncLearn.AsynLearning
import zionomic.errorhandling.DefectHandling
import zionomic.resourceLearning.ResourceLearning

import java.io.IOException
import java.util.UUID
import scala.io.Source

object Mai extends App {
  def attempt[A](a: => A) = {
    println(a)
    println(a)
  }

  def attenotNornal[A](a: A) = {
    println(a)
    println(a)
  }
  //attenotNornal({println("eval");5})
  attempt({
    println("eval");
    5
  })
}


object FiberLearning extends ZIOAppDefault {


  lazy val doSomething = for {
    _ <- ZIO.debug(s"do something")
    fibeer <- childSomething
  } yield ()

  lazy val childSomething =
    ZIO.debug(s"child doing something..").repeat(Schedule.spaced(1.seconds))

  def slowEffect(id: String) =
    for {
      _ <- printLine(s"Starting $id")
      _ <- ZIO.sleep(1.second) // Simulate work
      _ <- printLine(s"Finished $id")
    } yield id

  // Test program
  val program =
    ZIO.collectAllPar(
      List(
        slowEffect("Task1"),
        slowEffect("Task2"),
        slowEffect("Task3")
      )
    ).timed.map { case (duration, result) =>
      println(s"Completed all tasks in ${duration.toMillis}ms: $result")
      result
    }

  override def run = {
    //    for {
    //      _ <- zio.Console.printLine(s"started..")
    //      fiber <- doSomething.fork
    //      _ <- ZIO.sleep(10.seconds)
    //      _ <- ZIO.debug(s"resume dleep")x
    //      //      a <- fiber.await
    //      //      p <- fiber.poll
    //      i <- fiber.interrupt
    //      _ <- i.foldExitZIO( // Handle the result of the fiber
    //        e => ZIO.debug("The fiber has failed with: " + e),
    //        s => ZIO.debug("The fiber has completed with: " + s)
    //      )
    //      _ <- ZIO.never
    //    } yield ()

    /*for {
      _ <- ZIO.debug(s"the staRT")
      _ <- doSomething
    } yield ()*/

    //    program.exitCode



    //    ResourceLearning

 

   /* AsynLearning.program *>*//* effect.orDie.foldCauseZIO(
      cause => ZIO.succeed(s"Handled cause: ${cause.prettyPrint}"),
      value => ZIO.succeed(s"Success: ${value.mkString}")
    ).forEachZIO(s => ZIO.debug(s)) *>*/

  DefectHandling.transformed
  }


  /*


  import zio._
  //import zio.http._
  import java.util.concurrent.{ThreadPoolExecutor, TimeUnit, LinkedBlockingQueue}
  import java.sql.{Connection, DriverManager}
  import ZIO.blocking
  // Simulated services
  trait DatabaseService {
    def fetchUser(id: Int): ZIO[Any, Throwable, String]
  }

  trait ComputeService {
    def generateReport(data: List[Int]): ZIO[Any, Throwable, String]
  }

  object DatabaseService {
    val live: ZLayer[Blocking, Nothing, DatabaseService] = ZLayer {
      ZIO.serviceWith[Blocking](blocking =>
        new DatabaseService {
          def fetchUser(id: Int): ZIO[Any, Throwable, String] =
            blocking.blocking {
              ZIO.succeed {
                // Simulate a blocking JDBC call
                Thread.sleep(100) // Pretend this is a real DB query
                s"User-$id"
              }
            }
        }
      )
    }
  }

  object ComputeService {
    val live: ZLayer[Any, Nothing, ComputeService] = ZLayer {
      ZIO.succeed(
        new ComputeService {
          def generateReport(data: List[Int]): ZIO[Any, Throwable, String] =
            ZIO.succeed {
              // Simulate CPU-intensive work
              data.sum.toString
            }.onExecutor(cpuExecutor)
        }
      )
    }

    // Custom Executor for CPU-bound tasks
    private val cpuExecutor: Executor = {
      val threadPool = new ThreadPoolExecutor(
        Runtime.getRuntime.availableProcessors(), // Match CPU cores
        Runtime.getRuntime.availableProcessors(),
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue[Runnable]()
      )
      Executor.fromThreadPoolExecutor(threadPool)
    }
  }

  object WebServerExample extends ZIOAppDefault {
    // Custom Executor for non-blocking tasks (default runtime is fine, but we could customize)
    override val bootstrap: ZLayer[Any, Nothing, Unit] =
      Runtime.setExecutor(
        Executor.fromThreadPoolExecutor(
          new ThreadPoolExecutor(
            8, // Small pool for non-blocking tasks
            8,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue[Runnable]()
          )
        )
      )

    // HTTP routes
    val app: HttpApp[DatabaseService & ComputeService, Nothing] = Http.collectZIO[Request] {
      case Method.GET -> Root / "user" / int(id) =>
        ZIO.serviceWithZIO[DatabaseService](_.fetchUser(id))
          .map(name => Response.text(s"Fetched: $name"))
      case Method.GET -> Root / "report" =>
        ZIO.serviceWithZIO[ComputeService](_.generateReport(List(1, 2, 3, 4, 5)))
          .map(result => Response.text(s"Report: $result"))
    }

    // Run the server
    override def run: ZIO[Any, Throwable, Unit] =
      Server
        .serve(app)
        .provide(
          Server.defaultWithPort(8080),
          DatabaseService.live,
          ComputeService.live,
          ZLayer.succeed(Blocking.live)
        )
  */
  //}
}

object Main extends App {

  trait Bala[A] {
    self =>
    def printBala[A](value: Bala[A]) = {
      println(s"the self ${self.value} and the value ${value.value}")
      zipBala((self.value, value.value))
    }

    def value: A

    def zipBala[B >: A](s: (B, B)) = {
      BalaImpl(s)
    }

    def flatten = {
    }
  }

  class BalaImpl[A](a: A) extends Bala[A] {
    override def value: A = {
      a
    }
  }

  println(BalaImpl(10).printBala(BalaImpl[Int](11)).printBala(BalaImpl(21)).printBala(BalaImpl(33)).value)

}