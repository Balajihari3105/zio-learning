package zionomic.resourceLearning

import com.google.protobuf.compiler.plugin.CodeGeneratorResponse.File
import zio.{Queue, Scope, UIO, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.ZIO.{Acquire, debug, log, logInfo}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.util.*

object ResourceLearning extends ZIOAppDefault {

  trait Resources

  lazy val acquire: Future[Resources] = ???

  def use(acquire: Resources): Future[String] = ???

  def release(acquire: Resources): Future[Unit] = ???

  //extension [A]
  implicit final class FutureSyntax[+A](future: Future[A]) {
    def ensuring(finaliser: Future[Any])(using ec: ExecutionContext) = {
      future.transformWith {
        case Success(value) =>
          finaliser.flatMap(_ => Future.successful(value))
        case Failure(exception) => finaliser.flatMap(_ => Future.failed(exception))
      }
    }
  }

  lazy val exampleOFResouceInFuture = acquire.flatMap(s => use(s).ensuring(release(s)))


  def readAFile = ZIO.acquireReleaseWith(ZIO.attempt(Source.fromFile("src/main/scala/zionomic/resourceLearning/TestingFile.txt")))(s => ZIO.logInfo("resource closing....") *> ZIO.attempt(s.close()).orDie) {
    s =>
      val strr =  s.mkString
      zio.Console.printLine(strr).as(s"vkevkfvnj ${strr}")
  }

  import zio.durationInt
  def byName(f  : =>  zio.UIO[Unit])={
    ZIO.sleep(5.second)*> f
  }

  def aqrwE() = ZIO.acquireReleaseExit(
      ZIO.attempt(
        Source.fromFile("src/main/scala/zionomic/resourceLearning/TestingFile.txt")))
    ((s,e) => ZIO.logInfo("resource closing....") *> ZIO.attempt(s.close()).orDie)




  def aqr() = ZIO.acquireRelease(
      ZIO.attempt(
        Source.fromFile("src/main/scala/zionomic/resourceLearning/TestingFile.txt")))
    (s => ZIO.logInfo("resource closing....") *> ZIO.attempt(s.close()).orDie)

  def wFinal() = {
    ZIO.succeed("hello World").withFinalizer(r => debug(s"closing resource $r"))
  }
  
  override def run = ZIO.scoped{
    readAFile.forEachZIO(s => logInfo(s"the acquireReleaseWith returns ${s}")) *>
      aqrwE().forEachZIO(s => ZIO.logInfo(s"the filnal return ${s.mkString}"))
    *> byName(ZIO.debug(s"the by Name")) 
      *> aqr()
      *> wFinal()
      .tap(s => debug(s"the result of finalizer $s"))
  }
  
  
}

object ZioRunExample extends App {

  import zio._

  case class Config(value: String)

  val layer: ZLayer[Any, Nothing, Config] = ZLayer.succeed(Config("test"))
  val effect: ZIO[Config, Nothing, Unit] = ZIO.serviceWith[Config](c => println(c.value))

  val runtime: Runtime[Config] = Unsafe.unsafe { implicit u =>
    Runtime.unsafe.fromLayer(layer)
  }
  val result: Unit = Unsafe.unsafe { implicit u =>
    runtime.unsafe.run(effect).getOrThrowFiberFailure()
  }
}
