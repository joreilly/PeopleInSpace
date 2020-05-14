import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.surrus.common.repository.PeopleInSpaceRepository
import com.surrus.common.remote.PeopleInSpaceApi
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import kotlinx.coroutines.flow.collect


fun main() {
    val repository = PeopleInSpaceRepository()
    val peopleInSpaceApi = PeopleInSpaceApi()

    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }

        routing {

            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }

            static("/") {
                resources("")
            }

            get("/people") {
                repository.fetchPeopleAsFlow()?.collect {
                    call.respond(it)
                }
            }

            get("/astros.json") {
                val result = peopleInSpaceApi.fetchPeople()
                call.respond(result)
            }

        }
    }.start(wait = true)
}
