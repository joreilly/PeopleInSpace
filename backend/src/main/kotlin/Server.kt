import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.AstroResult
import com.surrus.common.remote.PeopleInSpaceApi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    val koin = initKoin(enableNetworkLogs = true).koin
    val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()
    peopleInSpaceApi.baseUrl = "http://api.open-notify.org"

    val port = System.getenv().getOrDefault("PORT", "8080").toInt()
    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }

        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            // header("any header") if you want to add any header
            allowCredentials = true
            allowNonSimpleContentTypes = true
            anyHost()
        }

        routing {

            get("/astros.json") {
                val ar = peopleInSpaceApi.fetchPeople()
                val result = AstroResult(ar.message, ar.number, ar.people.map {
                    val personImageUrl = personImages[it.name]
                    val personBio = personBios[it.name]
                    Assignment(it.craft, it.name, personImageUrl, personBio)
                })
                call.respond(result)
            }

            get("/iss-now.json") {
                val result = peopleInSpaceApi.fetchISSPosition()
                call.respond(result)
            }

            get("/astros_local.json") {
                val result = AstroResult(
                    "success", 3,
                    listOf(
                        Assignment("ISS", "Chris Cassidy"),
                        Assignment("ISS", "Anatoly Ivanishin"),
                        Assignment("ISS", "Ivan Vagner")
                    )
                )
                call.respond(result)
            }
        }
    }.start(wait = true)
}
