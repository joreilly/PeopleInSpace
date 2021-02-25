import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.HelloReply
import io.grpc.examples.helloworld.HelloRequest
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.grpc.VertxServerBuilder

class BackendApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            HelloWorldServer(12345).start()
        }
    }
}

class HelloVerticle : AbstractVerticle() {
    override fun start() {
        HelloWorldServer(12345).start()
    }
}
//fun main() {
//    val vertx = Vertx.vertx()
////    vertx.deployVerticle(HelloVerticle()) // TODO coroutine
//
//
//    val gRPCServer = VertxServerBuilder
//        .forPort(vertx, 12345)
//        .addService(GreeterService())
//        .build()
//
//    gRPCServer.start { ar ->
//        if (ar.failed()) {
//            ar.cause().printStackTrace()
//        }
//    }


//    val koin = initKoin(enableNetworkLogs = true).koin
//    val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()
//
//    embeddedServer(Netty, 9090) {
//        install(ContentNegotiation) {
//            json()
//        }
//
//        routing {
//
//            get("/") {
//                call.respondText(
//                    this::class.java.classLoader.getResource("index.html")!!.readText(),
//                    ContentType.Text.Html
//                )
//            }
//
//            static("/") {
//                resources("")
//            }
//
//            get("/astros.json") {
//                val result = peopleInSpaceApi.fetchPeople()
//                call.respond(result)
//            }
//
//            get("/astros_local.json") {
//                val result = AstroResult("success", 3,
//                    listOf(Assignment("ISS", "Chris Cassidy"),
//                        Assignment("ISS", "Anatoly Ivanishin"),
//                        Assignment("ISS", "Ivan Vagner")))
//                call.respond(result)
//            }
//
//        }
//    }.start(wait = true)
//}