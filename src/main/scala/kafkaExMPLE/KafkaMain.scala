package kafkaExMPLE

import zio.*
import zio.kafka.consumer.*
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.*
import zio.stream.ZStream
import zio.config.*
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

// Configuration classes
case class Cred(url: String, username: String, password: String)
case class Database(database: Cred)
case class Myapp(myapp: Database)

object Config {
  // Define the configuration descriptor for Myapp
  val configDescriptor: Config[Myapp] = deriveConfig[Myapp]

  // Load the configuration from application.conf
  def loadConfig: RIO[Any, Myapp] = {
    val source = TypesafeConfigProvider.fromResourcePath() // Use TypesafeConfigProvider
    read(configDescriptor from source)
  }
}

object MainApp extends ZIOAppDefault {

  // Producer stream
  val producer: ZStream[Producer, Throwable, Nothing] =
   val c= ZIO.config(Config.configDescriptor)
    ZStream
      .repeatZIO(Random.nextIntBetween(0, Int.MaxValue))
      .schedule(Schedule.fixed(2.seconds))
      .mapZIO { random =>
        Producer.produce[Any, Long, String](
          topic = "random",
          key = random % 4,
          value = random.toString,
          keySerializer = Serde.long,
          valueSerializer = Serde.string
        )
      }
      .drain
  

  // Consumer stream
  val consumer: ZStream[Consumer, Throwable, Nothing] =
    Consumer
      .plainStream(Subscription.topics("random"), Serde.long, Serde.string)
      .tap(r => Console.printLine(r.value))
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .drain

  
  // Producer layer with settings from config
  def producerLayer(database: Database): ZLayer[Any, Throwable, Producer] =
    ZLayer.scoped(
      Producer.make(
        settings = ProducerSettings(List("localhost:29092"))
      )
    )


  // Consumer layer with settings from config
  def consumerLayer(database: Database): ZLayer[Any, Throwable, Consumer] =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(List("localhost:29092")).withGroupId("group")
      )
    )

  override def run =
    for {
      myapp <- Config.loadConfig // Load configuration
      _ <- Console.printLine(myapp)
      _ <- producer.merge(consumer)
        .runDrain
        .provide(producerLayer(myapp.myapp), consumerLayer(myapp.myapp))
//      s <- consumer.runDrain.provide(producerLayer(myapp.myapp), consumerLayer(myapp.myapp))
    } yield ()
}

