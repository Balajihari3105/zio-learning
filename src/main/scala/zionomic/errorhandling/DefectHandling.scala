package zionomic.errorhandling

import zio.{Cause, ZIO}
import zio.ZIO.*

import java.io.IOException
import scala.io.Source

object DefectHandling {

  def effect:ZIO[Any, Throwable, String] = ZIO.failCause(Cause.die(throw IOException("io exception")))//attempt(Source.fromFile("/Users/bala/zio-learning/src/main/scala/zionomic/resourceLearning/TestingFile.txt"))

  def transformed = effect.unrefineWith { ipo =>
    ipo match
      case ioe: IOException =>
        println(ioe.getMessage)
        s"IO Error: ${ioe.getMessage}"
  } { ex =>
    s"Other error: ${ex.getMessage}"
  }

  def transform() =
    transformed.foldCauseZIO(
      f => debug(f),
      s => debug(s)
    )

}
