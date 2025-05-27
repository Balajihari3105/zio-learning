package zionomic.clockLearning

import zio.{Clock, ZIO}

import java.io.{File, FileWriter}

object ClockLearn {

  def clockEx() = {
    Clock.currentDateTime
  }

  def envVariablt = {
    zio.System.env("ENV")
  }

  def writeToFile(file: String, text: String): zio.ZIO[Any, Throwable, Unit] = {
    zio.ZIO.acquireReleaseWith {
      zio.ZIO.attempt(new FileWriter(new File(file)))
    }(f => zio.ZIO.attempt(f.close()).orDie)(pw => zio.ZIO.attempt(pw.write(text)))
  }

 /* final case class ZIO[-R, +E, +A](run: R => Either[E, A])

  def zipWith[R, E, A, B, C](
                              self: ZIO[R, E, A],
                              that: ZIO[R, E, B]
                            )(f: (A, B) => C): ZIO[R, E, C] =
    ZIO: r =>
      self.run(r) match
        case Left(e) => Left(e) // If self fails, propagate the error
        case Right(a) => that.run(r) match // If self succeeds, run that
          case Left(e) => Left(e) // If that fails, propagate the error
          case Right(b) => Right(f(a, b)) // If both succeed, apply f
*/

  def doWhile[R,E,A] (body : zio.ZIO[R,E,A])(f : A => Boolean): zio.ZIO[R, E, A]={
    body
  }.repeatUntil(s => f(s))
}

