import dev.johnoreilly.common.remote.Assignment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

fun createSpaceDevsHttpClient(engine: HttpClientEngine) = HttpClient(engine) {
    // Fail on 429 etc. so a rate-limited response falls back instead of being parsed.
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
    }
}

/**
 * People currently in space, from The Space Devs Launch Library 2 API.
 *
 * The free tier allows ~15 requests/hour and each refresh makes two, so results are
 * cached for [cacheTtl]. If a refresh fails, the last good result (or [fallback], before
 * the first success) is served and the refresh is retried after [retryAfterFailure].
 */
class SpaceDevsPeopleRepository(
    private val client: HttpClient,
    private val fallback: List<Assignment>,
    private val timeSource: TimeSource = TimeSource.Monotonic,
    private val cacheTtl: Duration = 1.hours,
    private val retryAfterFailure: Duration = 15.minutes,
    private val baseUrl: String = "https://ll.thespacedevs.com/2.2.0",
) {
    private val logger = LoggerFactory.getLogger(SpaceDevsPeopleRepository::class.java)
    private val mutex = Mutex()
    private var cached: List<Assignment>? = null
    private var nextRefresh: TimeMark? = null

    // Holding the lock across the fetch means concurrent requests share one refresh.
    suspend fun currentPeople(): List<Assignment> = mutex.withLock {
        if (nextRefresh?.hasPassedNow() != false) {
            try {
                cached = fetchPeople()
                nextRefresh = timeSource.markNow() + cacheTtl
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.warn("Fetching people from The Space Devs failed; serving cached/fallback list", e)
                nextRefresh = timeSource.markNow() + retryAfterFailure
            }
        }
        cached ?: fallback
    }

    private suspend fun fetchPeople(): List<Assignment> {
        val astronauts = client.get("$baseUrl/astronaut/") {
            parameter("format", "json")
            parameter("in_space", true)
            parameter("is_human", true)
            parameter("mode", "detailed")
            parameter("limit", 100)
        }.body<Page<Astronaut>>().results

        val stations = client.get("$baseUrl/spacestation/") {
            parameter("format", "json")
            parameter("status", ACTIVE_STATION_STATUS)
            parameter("mode", "detailed")
            parameter("limit", 20)
        }.body<Page<SpaceStation>>().results

        // Someone is always aboard the ISS, so an empty list means bad data, not an empty sky.
        check(astronauts.isNotEmpty()) { "The Space Devs returned no people in space" }

        val stationByAstronautId = stations
            .flatMap { station -> station.activeExpeditions.flatMap { it.crew }.map { it.astronaut.id to station.name } }
            .toMap()

        return astronauts.map { astronaut ->
            Assignment(
                craft = stationByAstronautId[astronaut.id]?.let(::craftName) ?: astronaut.currentMissionName() ?: "Unknown",
                name = astronaut.name,
                personImageUrl = astronaut.profileImageThumbnail ?: astronaut.profileImage ?: "",
                personBio = astronaut.bio ?: "",
                nationality = astronaut.nationality ?: "",
            )
        }
    }

    // Keep the short names the apps have always shown.
    private fun craftName(stationName: String) = when (stationName) {
        "International Space Station" -> "ISS"
        "Tiangong space station" -> "Tiangong"
        else -> stationName
    }

    // For someone not on a station (e.g. a free-flying mission). `flights` isn't sorted by date.
    private fun Astronaut.currentMissionName() =
        flights.firstOrNull { it.net == lastFlight }?.mission?.name

    private companion object {
        const val ACTIVE_STATION_STATUS = 1
    }
}

@Serializable
private class Page<T>(val results: List<T>)

@Serializable
private class Astronaut(
    val id: Int,
    val name: String,
    val nationality: String? = null,
    val bio: String? = null,
    @SerialName("profile_image") val profileImage: String? = null,
    @SerialName("profile_image_thumbnail") val profileImageThumbnail: String? = null,
    @SerialName("last_flight") val lastFlight: String? = null,
    val flights: List<Flight> = emptyList(),
)

@Serializable
private class Flight(val net: String? = null, val mission: Mission? = null)

@Serializable
private class Mission(val name: String? = null)

@Serializable
private class SpaceStation(
    val name: String,
    @SerialName("active_expeditions") val activeExpeditions: List<Expedition> = emptyList(),
)

@Serializable
private class Expedition(val crew: List<CrewMember> = emptyList())

@Serializable
private class CrewMember(val astronaut: CrewAstronaut)

@Serializable
private class CrewAstronaut(val id: Int)
