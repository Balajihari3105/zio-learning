/*import zio.
import zio.Console.*

import java.io.IOException
import scala.io.{Codec, Source}


object MyApp extends ZIOAppDefault {
  /*  val program11: ZIO[Any, IOException, Unit] = for {
      _ <- printLine("Hello, what is you name?")
      name <- readLine
      _ <- printLine(s"Hello $name, welcome to ZIO!")
    } yield ()*/

  def run: ZIO[Any, IOException, Unit] = {


    
  val lines = "hwllo this is a zio \n just learn the beauty"
  for {
    _ <- printLine("Hello! What is your name?")
    name <- readLine
    _ <- printLine(s"Hello, ${name}, welcome to ZIO!")
  } yield ()
  


    /*  Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.run(ZIO.attempt(println("Hello World!"))).getOrThrowFiberFailure()
  }*/
    //  val s = Console.readLine.flatMap(line => Console.printLine(line))

    val s1 = ZIO.succeed(10)

    val f1 = ZIO.fail("Uh,Oh!")
    val f2 = ZIO.fail(new Exception("Uh oh!"))
    Console.printError(f2)

    val o1 = ZIO.fromOption(Some(2))

    val o1h = o1.orElseFail("It wast there")

    val maybeId: ZIO[Any, Option[Nothing], String] = ZIO.fromOption(Some("abc123"))

    case class User(teamId: String)

    case class Team()

    def getUser(userId: String): ZIO[Any, Throwable, Option[User]] = ???

    def getTeam(teamId: String): ZIO[Any, Throwable, Team] = ???


    /*
  val getUsr = getUser("")
  val result: ZIO[Any, Throwable, Option[(User, Team)]] = (for {
  id <- maybeId
  user <- getUser(id).some
  team <- getTeam(user.teamId).asSomeError
  } yield (user, team)).unsome

  import scala.concurrent.Future

  lazy val future = Future.successful("Hello!")

  val zFuture = ZIO.fromFuture(implicit ec => future.map(s => s + "!!!"))

  */
    case class AuthError()

    object legacy {
      def login(
                 onSuccess: User => Unit,
                 onFailure: AuthError => Unit): Unit = ???
    }

    val login: ZIO[Any, AuthError, User] = {
      ZIO.async(callback =>
        legacy.login(
          user => callback(ZIO.succeed(user)),
          err => callback(ZIO.fail(err))
        )
      )
    }


    def download(url: String) = {
      ZIO.attempt(
        Source.fromURL(url)(Codec.UTF8).mkString
      )
    }

    def safeDownload(url: String) = {
      ZIO.blocking(download(url))
    }

    val mapS = ZIO.succeed(21).map(_ * 2).forEachZIO(ex => Console.printLine(ex))

    val mapF = ZIO.fail("Oh.uh").mapError(s => s + "!!!!")

    mapF.fold(err => Console.printLine(err),
      suc => Console.printLine(suc))


    for {
      _ <- ZIO.blocking(Console.printLine("hello"))
      _ <- ZIO.blocking(Console.printLine("hwlo"))
      _ <- Console.readLine.flatMap(input => Console.printLine(s"You entered: $input"))
      mas = ZIO.fail("Oh.uh").mapError(s => s + "!!!!")
      _ = mapF.fold(err => Console.printLine(err),
        suc => Console.printLine(suc))
    } yield ()

    Console.readLine.flatMap(input => Console.printLine(s"You entered: $input"))
  }

}*/