package grpcDemo

import io.grpc.ServerBuilder
import io.grpc.examples.helloworld.hello.GreeterGrpc.Greeter
import io.grpc.examples.helloworld.hello.{HelloReply, HelloRequest}
import zio.{ZIO, ZIOAppDefault}

import scala.concurrent.Future

object HelloWorldServer extends ZIOAppDefault {
  override def run = {
    val server = ServerBuilder
      .forPort(9000)
      .addService(Greeter.bindService(new GreeterImpl, scala.concurrent.ExecutionContext.Implicits.global))
      .build.start

    ZIO.never
  }
}

class GreeterImpl extends Greeter {
  override def sayHello(request: HelloRequest): Future[HelloReply] = {
    Future.successful(HelloReply(s"Hello, ${request.name}"))
  }
}