import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object PromiseApp {
  def main(args: Array[String]): Unit = {
    val contact: Option[String] = Some("hello")
    val value : Option[String] = None

    val e = for{
      c <- contact
      _ =  println(s"hwllo $c")
      b <- value
      _ = println("thee world is oo")
    } yield b


    println(e)
  }
}
