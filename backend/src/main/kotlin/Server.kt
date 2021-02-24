import com.surrus.common.di.initKoin
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.common.remote.AstroResult
import com.surrus.common.remote.Assignment
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.vertx.core.Vertx
import io.grpc.examples.helloworld.GreeterGrpc
import io.grpc.examples.helloworld.HelloReply
import io.grpc.examples.helloworld.HelloRequest
import io.grpc.stub.StreamObserver
import io.vertx.core.AbstractVerticle
import io.vertx.grpc.VertxServerBuilder



//class Service : GreeterGrpc.GreeterImplBase() {
//    override fun sayHello(request: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
//        val reply = HelloReply.newBuilder().setMessage("Hello " + request.name).build()
//        responseObserver.onNext(reply)
//        responseObserver.onCompleted()
//    }
//}
//
//class HelloVerticle : AbstractVerticle() {
//    private val dao = IslandsDao()

//    private val router = Router.router(vertx).apply {

    // Convenience method so you can run it in your IDE
//    @JvmStatic
//    fun main(args: Array<String>) {
//        Runner.runExample(Server::class.java)
//    }

//    override fun start() {

    //        val server = VertxServerBuilder.forAddress(vertx, "localhost", 8080).addService(Service()).build()
//        server.start { ar ->
//            if (ar.succeeded()) {
//                println("gRPC service started")
//            } else {
//                println("Could not start server " + ar.cause().message)
//            }
//        }
//    }
//    open fun start() {
//    }
//}