package catslearning

import cats.effect.{IO, IOApp}
import cats.effect.cps.{async, await}
import cats.effect.cps.catsEffectCpsConcurrentMonad

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import cats.effect.*
import cats.implicits._
object HelloWorld extends IOApp.Simple {

  type Proposition =  String => Hello
  
  object Proposition{
    
    def apply(f : String => Hello) = f
  }
  
  def fun = Hello("hello")

  
  case class HeaderNotFound(name:String)

  case class Hello(name: String)
  val m1  = Map("someheeader"->"huyu vishal")



  def flatMapAwiat()={
    async[IO]{
      IO.println("line 1")
      IO.println("line 2").andWait(1.seconds).await
      IO.println("line 3").await
    }
  }

  def asyncCallBackFunction()={
    import java.util.concurrent.{Executors, TimeUnit}

    val scheduler = Executors.newScheduledThreadPool(1)
    IO.async_[Unit]{callback =>
      scheduler.schedule(new Runnable{
        override def run(): Unit = println("executed")
      },500, TimeUnit.MILLISECONDS)
      ()
    }
  }

  import cats.Monad
  import cats.effect.std.Console
  import cats.syntax.all._

  def example[F[_] : Monad : Console](str: String): F[String] = {
    val printer: F[Unit] = Console[F].println(str)
    (printer >> printer).as(str)
  }


  def function(num: Int): List[List[Int]] ={
    println(num)
    List(List(num+1))
  }


  val run =
    for {
      ctr <- IO.ref(0)

//     /* wait = IO.sleep(1.second)
//      poll = wait *> ctr.get

//      _ <- poll.flatMap(IO.println(_)).foreverM.start
//      _ <- poll.map(_ % 3 == 0).ifM(IO.println("fizz"), IO.unit).foreverM.start
//      _ <- poll.map(_ % 5 == 0).ifM(IO.println("buzz"), IO.unit).foreverM.start
//      _ <- IO.println("hellow") *> IO.println("World")
//      _ <- flatMapAwiat()
//      _ <- (wait *> ctr.update(_ + 1)).foreverM.void*/
      s = List(1,2,3,4,5).flatTraverse(function)
      _ <- IO.println(s"the lsit $s")
    } yield ()
}