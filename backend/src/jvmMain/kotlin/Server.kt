import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.AstroResult
import dev.johnoreilly.common.remote.IssResponse
import dev.johnoreilly.common.remote.PeopleInSpaceApi
import io.ktor.http.*
import io.ktor.openapi.OpenApiInfo
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.describe
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
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
            swaggerUI("/docs") {
                info = OpenApiInfo("PeopleinSpace API", "1.0.0")
            }

            get("/astros.json") {
                val result = AstroResult("success", currentPeopleInSpace.size, currentPeopleInSpace)
                call.respond(result)
            }.describe {
                summary = "Get a list of people in space"
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<AstroResult>()
                    }
                }
            }

            get("/iss-now.json") {
                val result = peopleInSpaceApi.fetchISSPosition()
                call.respond(result)
            }.describe {
                summary = "Get the position of the ISS"
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<IssResponse>()
                    }
                }
            }

            get("/astros_local.json") {
                val result = AstroResult(
                    "success", 3,
                    listOf(
                        Assignment("ISS", "Chris Cassidy"),
                        Assignment("ISS", "Tracy Caldwell Dyson"),
                        Assignment("ISS", "Ivan Vagner")
                    )
                )
                call.respond(result)
            }
        }
    }.start(wait = true)
}
