import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.HelloReply
import io.grpc.examples.helloworld.HelloRequest
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.grpc.VertxServerBuilder

fun main() {
    val vertx = Vertx.vertx()
//    vertx.deployVerticle(HelloVerticle()) // TODO coroutine
    // The rcp service
    // The rcp service
    val service: GreeterGrpcKt.GreeterCoroutineImplBase =
        object : GreeterGrpcKt.GreeterCoroutineImplBase() {
            fun sayHello(request: HelloRequest, future: Promise<HelloReply>) {
                future.complete(HelloReply.newBuilder().build())
            }
        }

    val gRPCServer = VertxServerBuilder
        .forPort(vertx, 12345)
        .addService(service)
        .build()

    gRPCServer.start { ar ->
        if (ar.failed()) {
            ar.cause().printStackTrace()
        }
    }


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
}