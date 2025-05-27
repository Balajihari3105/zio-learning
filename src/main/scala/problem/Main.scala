package problem


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try.*
import cats.implicits.*

object Main extends App {

  /* println(Problem.firstOccurence("sadbutsad", "sad"))

  println(Problem.searchInsert(Array(1, 3, 5, 6), 7))
  println(Problem.searchInsert(Array(1, 3, 5, 6), 6))

  println(parseEdge("[b-c]", '-')) // (List(Edge(b,c,Some(null))),List())
  println(parseEdge("[b-c/9]", '-'))
*//*

  val l = List(List(1,3))

  val g = List(List(1,2),List(1,3))

//    [[0,1],[1,2],[2,3],[3,4]]
//[[0,4],[4,0],[1,3],[3,0]]

val pe =new util.ArrayList[util.List[Int]](new util.ArrayList(0,1), new util.ArrayList[Int](1,2), util.ArrayList[Int](2,3), util.ArrayList[Int](3,4))
  val p = new Solution().checkIfPrerequisite( 2, pe, Array(Array(0,4),Array(4,0),Array(1,3), Array(3,0)))

  println(p)*/

  def callFunction(attempt: Int = 1): Future[Int] = {
    functionRetry(attempt).flatMap { res =>
      if (res < 3) Future.failed(new Throwable("Retrying..."))
      else Future.successful(11)
    }.recoverWith {
      case ex =>
        println(s"Error occurred: ${ex.getMessage}. Retrying...")
        callFunction(attempt + 1)
    }
  }

  def functionRetry(att: Int): Future[Int] = Future {
    println(s"Retrying with attempt: $att")
    if (att > 3) {
      23
    } else {
      att
    }
  }

  // Main loop
  lazy val futureLoop = Future {
    while (true) {
      callFunction().foreach(result => println(s"Result: $result"))
    }
  }

  // Shutdown hook
  Runtime.getRuntime.addShutdownHook(new Thread(() => {
    println("Exiting...")
  }))

  // Await termination of the loop (optional)
  //  Await.ready(futureLoop, Duration.Inf)


  def checkIf(field: Option[String]) = {
    field.fold(Option(false)) {
      value =>
        Either.catchNonFatal(value.toBoolean)
          .map(Some(_))
          .getOrElse(Some(false))
    }
  }

  println(checkIf(None))
}
