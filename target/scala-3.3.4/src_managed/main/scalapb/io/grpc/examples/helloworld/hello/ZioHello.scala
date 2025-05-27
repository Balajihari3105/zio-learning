package io.grpc.examples.helloworld.hello

object ZioHello {
  trait GGreeter[-Context, +Error] extends scalapb.zio_grpc.GenericGeneratedService[Context, Error, GGreeter] {
    self =>
    def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest, context: Context): _root_.zio.IO[Error, io.grpc.examples.helloworld.hello.HelloReply]
    
    def transform[Context1, Error1](f: scalapb.zio_grpc.GTransform[Context, Error, Context1, Error1]): io.grpc.examples.helloworld.hello.ZioHello.GGreeter[Context1, Error1] = new io.grpc.examples.helloworld.hello.ZioHello.GGreeter[Context1, Error1] {
      def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest, context: Context1): _root_.zio.IO[Error1, io.grpc.examples.helloworld.hello.HelloReply] = f.effect(self.sayHello(request, _))(context)
    }
  }
  
  type ZGreeter[Context] = GGreeter[Context, io.grpc.StatusException]
  type RCGreeter = GGreeter[scalapb.zio_grpc.RequestContext, io.grpc.StatusException]
  
  trait Greeter extends scalapb.zio_grpc.GeneratedService {
    self =>
    type Generic[-C, +E] = GGreeter[C, E]
    def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest): _root_.zio.IO[io.grpc.StatusException, io.grpc.examples.helloworld.hello.HelloReply]
    
    override def asGeneric: GGreeter[Any, io.grpc.StatusException] = new GGreeter[Any, io.grpc.StatusException] {
      def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest, context: Any): _root_.zio.IO[io.grpc.StatusException, io.grpc.examples.helloworld.hello.HelloReply] = self.sayHello(request)
    }
    def transform(z: scalapb.zio_grpc.Transform): GGreeter[Any, io.grpc.StatusException] = asGeneric.transform(z)
    def transform[C, E](z: scalapb.zio_grpc.GTransform[Any, io.grpc.StatusException, C, E]): GGreeter[C, E] = asGeneric.transform(z)
  }
  
  object Greeter {
    implicit val genericBindable: scalapb.zio_grpc.GenericBindable[io.grpc.examples.helloworld.hello.ZioHello.Greeter] = new scalapb.zio_grpc.GenericBindable[io.grpc.examples.helloworld.hello.ZioHello.Greeter] {
      def bind(serviceImpl: io.grpc.examples.helloworld.hello.ZioHello.Greeter): zio.UIO[_root_.io.grpc.ServerServiceDefinition] = io.grpc.examples.helloworld.hello.ZioHello.GGreeter.genericBindable.bind(serviceImpl.asGeneric)
    }
  }
  
  object GGreeter {
    implicit val genericBindable: scalapb.zio_grpc.GenericBindable[io.grpc.examples.helloworld.hello.ZioHello.GGreeter[scalapb.zio_grpc.RequestContext, io.grpc.StatusException]] = new scalapb.zio_grpc.GenericBindable[io.grpc.examples.helloworld.hello.ZioHello.GGreeter[scalapb.zio_grpc.RequestContext, io.grpc.StatusException]] {
      def bind(serviceImpl: io.grpc.examples.helloworld.hello.ZioHello.GGreeter[scalapb.zio_grpc.RequestContext, io.grpc.StatusException]): zio.UIO[_root_.io.grpc.ServerServiceDefinition] =
        zio.ZIO.runtime[Any].map {
          runtime =>
            _root_.io.grpc.ServerServiceDefinition.builder(io.grpc.examples.helloworld.hello.GreeterGrpc.SERVICE)
            .addMethod(
              io.grpc.examples.helloworld.hello.GreeterGrpc.METHOD_SAY_HELLO,
              _root_.scalapb.zio_grpc.server.ZServerCallHandler.unaryCallHandler(runtime, serviceImpl.sayHello(_, _))
            )
            .build()
        }
      }
  }
  
  
  // accessor methods
  class GreeterAccessors(transforms: scalapb.zio_grpc.ClientTransform = scalapb.zio_grpc.ClientTransform.identity) extends scalapb.zio_grpc.GeneratedClient[GreeterAccessors] {
    def this(callOptionsFunc: io.grpc.CallOptions => io.grpc.CallOptions, metadataFunc: scalapb.zio_grpc.SafeMetadata => zio.UIO[scalapb.zio_grpc.SafeMetadata]) = this(scalapb.zio_grpc.ClientTransform.mapCallOptions(callOptionsFunc).andThen(scalapb.zio_grpc.ClientTransform.mapMetadataZIO(metadataFunc)))
    def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest): _root_.zio.ZIO[GreeterClient, io.grpc.StatusException, io.grpc.examples.helloworld.hello.HelloReply] = _root_.zio.ZIO.serviceWithZIO[GreeterClient](_.transform(transforms).sayHello(request))
    override def transform(t: scalapb.zio_grpc.ClientTransform): GreeterAccessors = new GreeterAccessors(transforms.andThen(t))
  }
  
  trait GreeterClient extends scalapb.zio_grpc.GeneratedClient[GreeterClient] {
    self =>
    def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest): _root_.zio.IO[io.grpc.StatusException, io.grpc.examples.helloworld.hello.HelloReply]
    
    // Returns a client that gives access to headers and trailers from server's response
    def withResponseMetadata: GreeterClientWithResponseMetadata
  }
  
  object GreeterClient extends GreeterAccessors {
    
    private class ServiceStub(underlying: GreeterClientWithResponseMetadata)
        extends GreeterClient {
      def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest): _root_.zio.IO[io.grpc.StatusException, io.grpc.examples.helloworld.hello.HelloReply] = underlying.sayHello(request).map(_.response)
      // Returns a client that gives access to headers and trailers from server's response
      def withResponseMetadata: GreeterClientWithResponseMetadata = underlying
      override def transform(t: scalapb.zio_grpc.ClientTransform): GreeterClient = new ServiceStub(underlying.transform(t))
    }
    
    def scoped(managedChannel: scalapb.zio_grpc.ZManagedChannel, transforms: scalapb.zio_grpc.ClientTransform): zio.ZIO[zio.Scope, Throwable, GreeterClient] =
      GreeterClientWithResponseMetadata.scoped(managedChannel, transforms).map(client => new ServiceStub(client))
    def scoped(managedChannel: scalapb.zio_grpc.ZManagedChannel, options: io.grpc.CallOptions = io.grpc.CallOptions.DEFAULT, metadata: zio.UIO[scalapb.zio_grpc.SafeMetadata]=scalapb.zio_grpc.SafeMetadata.make): zio.ZIO[zio.Scope, Throwable, GreeterClient] =
      GreeterClient.scoped(managedChannel, scalapb.zio_grpc.ClientTransform.withCallOptions(options).andThen(scalapb.zio_grpc.ClientTransform.withMetadataZIO(metadata)))
    
    def live(managedChannel: scalapb.zio_grpc.ZManagedChannel, transforms: scalapb.zio_grpc.ClientTransform): zio.ZLayer[Any, Throwable, GreeterClient] =
      GreeterClientWithResponseMetadata.live(managedChannel, transforms).map(clientEnv => zio.ZEnvironment(new ServiceStub(clientEnv.get)))
    def live(managedChannel: scalapb.zio_grpc.ZManagedChannel, options: io.grpc.CallOptions=io.grpc.CallOptions.DEFAULT, metadata: zio.UIO[scalapb.zio_grpc.SafeMetadata] = scalapb.zio_grpc.SafeMetadata.make): zio.ZLayer[Any, Throwable, GreeterClient] =
      GreeterClient.live(managedChannel, scalapb.zio_grpc.ClientTransform.withCallOptions(options).andThen(scalapb.zio_grpc.ClientTransform.withMetadataZIO(metadata)))
  }
  
  // accessor with metadata methods
  class GreeterWithResponseMetadataAccessors(transforms: scalapb.zio_grpc.ClientTransform = scalapb.zio_grpc.ClientTransform.identity) extends scalapb.zio_grpc.GeneratedClient[GreeterWithResponseMetadataAccessors] {
    def this(callOptionsFunc: io.grpc.CallOptions => io.grpc.CallOptions, metadataFunc: scalapb.zio_grpc.SafeMetadata => zio.UIO[scalapb.zio_grpc.SafeMetadata]) = this(scalapb.zio_grpc.ClientTransform.mapCallOptions(callOptionsFunc).andThen(scalapb.zio_grpc.ClientTransform.mapMetadataZIO(metadataFunc)))
    def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest): _root_.zio.ZIO[GreeterClientWithResponseMetadata, io.grpc.StatusException, scalapb.zio_grpc.ResponseContext[io.grpc.examples.helloworld.hello.HelloReply]] = _root_.zio.ZIO.serviceWithZIO[GreeterClientWithResponseMetadata](_.transform(transforms).sayHello(request))
    override def transform(t: scalapb.zio_grpc.ClientTransform): GreeterWithResponseMetadataAccessors = new GreeterWithResponseMetadataAccessors(transforms.andThen(t))
  }
  
  trait GreeterClientWithResponseMetadata extends scalapb.zio_grpc.GeneratedClient[GreeterClientWithResponseMetadata] {
    self =>
    def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest): _root_.zio.IO[io.grpc.StatusException, scalapb.zio_grpc.ResponseContext[io.grpc.examples.helloworld.hello.HelloReply]]
  }
  
  object GreeterClientWithResponseMetadata extends GreeterWithResponseMetadataAccessors {
    private class ServiceStub(channel: scalapb.zio_grpc.ZChannel, transforms: scalapb.zio_grpc.ClientTransform)
        extends GreeterClientWithResponseMetadata {
      def sayHello(request: io.grpc.examples.helloworld.hello.HelloRequest): _root_.zio.IO[io.grpc.StatusException, scalapb.zio_grpc.ResponseContext[io.grpc.examples.helloworld.hello.HelloReply]] =
        scalapb.zio_grpc.SafeMetadata.make.flatMap { metadata =>
          transforms.effect { context => 
            scalapb.zio_grpc.client.ClientCalls.withMetadata.unaryCall[io.grpc.examples.helloworld.hello.HelloRequest, io.grpc.examples.helloworld.hello.HelloReply](
              channel,
              context.method.asInstanceOf[io.grpc.MethodDescriptor[io.grpc.examples.helloworld.hello.HelloRequest, io.grpc.examples.helloworld.hello.HelloReply]],
              context.options,
              context.metadata,
              request
            )
          }(scalapb.zio_grpc.ClientCallContext(io.grpc.examples.helloworld.hello.GreeterGrpc.METHOD_SAY_HELLO, io.grpc.CallOptions.DEFAULT, metadata))
        }
      override def transform(t: scalapb.zio_grpc.ClientTransform): GreeterClientWithResponseMetadata = new ServiceStub(channel, transforms.compose(t))
    }
    
    def scoped(managedChannel: scalapb.zio_grpc.ZManagedChannel, transforms: scalapb.zio_grpc.ClientTransform): zio.ZIO[zio.Scope, Throwable, GreeterClientWithResponseMetadata] = managedChannel.map {
      channel => new ServiceStub(channel, transforms)
    }
    def scoped(managedChannel: scalapb.zio_grpc.ZManagedChannel, options: io.grpc.CallOptions = io.grpc.CallOptions.DEFAULT, metadata: zio.UIO[scalapb.zio_grpc.SafeMetadata]=scalapb.zio_grpc.SafeMetadata.make): zio.ZIO[zio.Scope, Throwable, GreeterClientWithResponseMetadata] =
      scoped(managedChannel, scalapb.zio_grpc.ClientTransform.withCallOptions(options).andThen(scalapb.zio_grpc.ClientTransform.withMetadataZIO(metadata)))
    
    def live[Context](managedChannel: scalapb.zio_grpc.ZManagedChannel, transforms: scalapb.zio_grpc.ClientTransform): zio.ZLayer[Any, Throwable, GreeterClientWithResponseMetadata] =
      zio.ZLayer.scoped(scoped(managedChannel, transforms))
    def live[Context](managedChannel: scalapb.zio_grpc.ZManagedChannel, options: io.grpc.CallOptions=io.grpc.CallOptions.DEFAULT, metadata: zio.UIO[scalapb.zio_grpc.SafeMetadata] = scalapb.zio_grpc.SafeMetadata.make): zio.ZLayer[Any, Throwable, GreeterClientWithResponseMetadata] =
      zio.ZLayer.scoped(scoped(managedChannel, options, metadata))
  }
}