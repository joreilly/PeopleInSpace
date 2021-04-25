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


fun main() {
    val koin = initKoin(enableNetworkLogs = true).koin
    val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()

    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }

        routing {

            get("/astros.json") {
                val result = peopleInSpaceApi.fetchPeople()
                call.respond(result)
            }

            get("/astros_local.json") {
                val result = AstroResult("success", 3,
                    listOf(Assignment("ISS", "Chris Cassidy"),
                        Assignment("ISS", "Anatoly Ivanishin"),
                        Assignment("ISS", "Ivan Vagner")))
                call.respond(result)
            }

        }
    }.start(wait = true)
}
