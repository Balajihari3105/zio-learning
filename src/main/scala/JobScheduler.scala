/*import zio._
import zio.clock.Clock
import zio.duration.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object JobScheduler extends ZIOAppDefault {

  // Define the job to be executed
  val job: UIO[Unit] = ZIO.logInfo("Executing job: It's Wednesday at 12 PM!")

  // Function to calculate the delay until the next Wednesday at 12 PM
  def nextWednesdayAtNoon: UIO[Duration] = ZIO.succeed {
    val now = LocalDateTime.now()
    val nextWednesday = now.`with`(ChronoUnit.DAYS, now.getDayOfWeek.getValue match {
      case 3 => now.getDayOfMonth // If today is Wednesday, stay on the same day
      case _ => now.getDayOfMonth + (3 - now.getDayOfWeek.getValue + 7) % 7 // Calculate days until next Wednesday
    }).withHour(12).withMinute(0).withSecond(0).withNano(0)

    if (nextWednesday.isBefore(now)) {
      nextWednesday.plusWeeks(1)
    } else {
      nextWednesday
    }

    Duration.fromDuration(java.time.Duration.between(now, nextWednesday))
  }

  // Main loop to run the job every Wednesday at 12 PM
  def scheduleJob: ZIO[Clock, Nothing, Unit] = {
    for {
      delay <- nextWednesdayAtNoon
      _ <- ZIO.sleep(delay) // Wait until the next Wednesday at 12 PM
      _ <- job // Execute the job
      _ <- scheduleJob // Schedule the next execution
    } yield ()
  }

  // Main run method
  def run: ZIO[Clock, Nothing, Unit] = scheduleJob
}*/