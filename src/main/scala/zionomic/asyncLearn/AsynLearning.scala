package zionomic.asyncLearn
import zio.ZIO
import zio.ZIO.*

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success,Failure}
object AsynLearning {

  // Simulating an asynchronous operation (e.g., a Future-based API call)
  def fetchDataFromExternalSource(id:Int) =  {
    Thread.sleep(1000) // Simulate delay
    if(id == 7) Future.successful("balaji")
    else Future.failed(Throwable("faield"))
  }

  // Converting a Future into a ZIO effect using ZIO.async
  val fetchData= ZIO.async { callback =>
    fetchDataFromExternalSource(8).onComplete {
      case Success(value) => callback(ZIO.some(value))
      case Failure(error) => callback(ZIO.none)
    }
  }

  // Program that uses the async effect
  val program = for {
    data <- fetchData
    _ <- ZIO.succeed(println(s"Received data: $data"))
  } yield ()

}
/*

import zio._
import zio.logging._
import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpResponse.BodyHandlers
import scala.util.{Try, Success, Failure}

// Define a case class for the user data
case class UserProfile(id: String, name: String)

// Configuration layer
case class AppConfig(apiEndpoint: String, timeoutSeconds: Int)

object AppConfig {
  val live: ZLayer[Any, Nothing, AppConfig] = ZLayer.succeed(
    AppConfig(
      apiEndpoint = "https://api.example.com/user",
      timeoutSeconds = 5
    )
  )
}

// Logger layer
object Logger {
  val live: ZLayer[Any, Nothing, Logging] = Logging.console(
    logLevel = LogLevel.Info,
    format = LogFormat.default
  )
}

// HTTP Client service
trait HttpService {
  def fetchUserProfile(userId: String): ZIO[Any, Throwable, UserProfile]
}

object HttpService {
  val live: ZLayer[AppConfig, Nothing, HttpService] = ZLayer {
    for {
      config <- ZIO.service[AppConfig]
      client <- ZIO.succeed(HttpClient.newBuilder().build())
    } yield new HttpService {
      def fetchUserProfile(userId: String): ZIO[Any, Throwable, UserProfile] = {
        val request = HttpRequest
          .newBuilder()
          .uri(URI.create(s"${config.apiEndpoint}/$userId"))
          .timeout(java.time.Duration.ofSeconds(config.timeoutSeconds.toLong))
          .GET()
          .build()

        ZIO.async { callback =>
          client
            .sendAsync(request, BodyHandlers.ofString())
            .whenComplete { (response, error) =>
              if (error != null) {
                callback(ZIO.fail(error))
              } else {
                Try(parseResponse(response.body())) match {
                  case Success(profile) => callback(ZIO.succeed(profile))
                  case Failure(e)      => callback(ZIO.fail(e))
                }
              }
            }
        }
      }

      private def parseResponse(body: String): UserProfile = {
        // Simplified parsing (in production, use a JSON library like circe)
        val parts = body.split(",")
        UserProfile(parts(0), parts(1))
      }
    }
  }
}

// Main application logic
object ProductionGradeExample extends ZIOAppDefault {

  // Business logic with retries and logging
  def fetchAndProcessUser(userId: String): ZIO[HttpService with Logging, Throwable, Unit] =
    for {
      _ <- log.info(s"Fetching profile for user: $userId")
      profile <- ZIO.serviceWithZIO[HttpService](_.fetchUserProfile(userId))
        .retry(Schedule.exponential(500.millis) && Schedule.recurs(3)) // Retry 3 times with exponential backoff
        .tapError(err => log.error(s"Failed to fetch profile: $err"))
      _ <- log.info(s"Successfully fetched profile: $profile")
    } yield ()

  // Program composition
  val program: ZIO[HttpService with Logging, Throwable, Unit] =
    fetchAndProcessUser("user123")

  // Layer composition
  val appLayer: ZLayer[Any, Nothing, HttpService with Logging] =
    ZLayer.make[HttpService with Logging](
      AppConfig.live,
      HttpService.live,
      Logger.live
    )

  // Run the program with proper layer provisioning
  def run = program.provideLayer(appLayer)
*/
