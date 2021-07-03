import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.AstroResult
import com.surrus.common.remote.PeopleInSpaceApi
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

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
            method(HttpMethod.Options)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.Authorization)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.AccessControlAllowOrigin)
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
