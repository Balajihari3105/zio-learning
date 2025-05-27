package grpcDemo

import io.grpc.examples.helloworld.hello.ZioHello.Greeter
import io.grpc.examples.helloworld.hello.{GreeterGrpc, HelloRequest}
import io.grpc.{Channel, ManagedChannelBuilder}
import zio.Console.printLine
import zio.{ExitCode, URIO, ZIO, ZIOAppDefault}

object HelloWorldClient extends ZIOAppDefault {
  override def run = {
    println(sys.env.get("keyValue"))
    val channel: Channel = ManagedChannelBuilder.forTarget("localhost:9000").usePlaintext.build
    val greeter = GreeterGrpc.stub(channel)

    for {
      response <- ZIO.fromFuture(implicit ec => greeter.sayHello(HelloRequest("John")))
      _ <- printLine(s"Greeting: ${response.message}")
    } yield ExitCode.success
  }
}