import dev.johnoreilly.common.remote.Assignment
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TestTimeSource

class SpaceDevsPeopleRepositoryTest {
    private val fallback = listOf(Assignment(craft = "ISS", name = "Fallback Person"))
    private val timeSource = TestTimeSource()
    private val requests = mutableListOf<String>()
    private var status = HttpStatusCode.OK

    private val engine = MockEngine { request ->
        requests += request.url.encodedPath
        respondJson(request)
    }

    private val repository = SpaceDevsPeopleRepository(
        client = createSpaceDevsHttpClient(engine),
        fallback = fallback,
        timeSource = timeSource,
    )

    @Test
    fun mapsAstronautsToTheirStationOrMission() = runTest {
        val people = repository.currentPeople()

        assertEquals(
            listOf(
                Assignment("ISS", "Jessica Watkins", "https://example.com/watkins_thumb.jpg", "Geologist.", "American"),
                Assignment("Tiangong", "Zhu Yangzhu", "https://example.com/zhu.jpg", "", "Chinese"),
                Assignment("Free Flyer 1", "Solo Flyer", "", "", ""),
            ),
            people,
        )
    }

    @Test
    fun cachesForAnHour() = runTest {
        repository.currentPeople()
        timeSource += 59.minutes
        repository.currentPeople()
        assertEquals(2, requests.size)

        timeSource += 2.minutes
        repository.currentPeople()
        assertEquals(4, requests.size)
    }

    @Test
    fun servesFallbackWhenFirstFetchFails() = runTest {
        status = HttpStatusCode.TooManyRequests
        assertEquals(fallback, repository.currentPeople())
    }

    @Test
    fun keepsLastGoodResultAndRetriesAfterFailure() = runTest {
        val good = repository.currentPeople()

        timeSource += 2.hours
        status = HttpStatusCode.TooManyRequests
        assertEquals(good, repository.currentPeople())
        val requestsAfterFailure = requests.size

        // Don't hammer the rate-limited API on every request...
        timeSource += 5.minutes
        repository.currentPeople()
        assertEquals(requestsAfterFailure, requests.size)

        // ...but retry once the backoff has passed.
        timeSource += 11.minutes
        status = HttpStatusCode.OK
        repository.currentPeople()
        assertEquals(requestsAfterFailure + 2, requests.size)
    }

    private fun MockRequestHandleScope.respondJson(request: HttpRequestData) = respond(
        content = if (status != HttpStatusCode.OK) """{"detail":"Request was throttled."}"""
        else if (request.url.encodedPath.endsWith("/astronaut/")) ASTRONAUTS else STATIONS,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

    private companion object {
        // Trimmed from real LL2 2.2.0 responses; unknown keys are kept to check they're ignored.
        val ASTRONAUTS = """
            {"count": 3, "next": null, "results": [
              {"id": 1, "name": "Jessica Watkins", "nationality": "American", "bio": "Geologist.", "in_space": true,
               "profile_image": "https://example.com/watkins.jpg", "profile_image_thumbnail": "https://example.com/watkins_thumb.jpg",
               "last_flight": "2026-10-01T15:10:06Z", "agency": {"id": 44, "name": "NASA"},
               "flights": [{"net": "2026-10-01T15:10:06Z", "mission": {"name": "Crew-13"}}]},
              {"id": 2, "name": "Zhu Yangzhu", "nationality": "Chinese", "bio": null,
               "profile_image": "https://example.com/zhu.jpg", "profile_image_thumbnail": null,
               "last_flight": "2026-05-24T15:08:36Z", "flights": []},
              {"id": 3, "name": "Solo Flyer", "last_flight": "2026-11-01T00:00:00Z",
               "flights": [{"net": "2026-11-01T00:00:00Z", "mission": {"name": "Free Flyer 1"}},
                           {"net": "2020-01-01T00:00:00Z", "mission": {"name": "Earlier Mission"}}]}
            ]}
        """.trimIndent()

        val STATIONS = """
            {"count": 2, "results": [
              {"id": 4, "name": "International Space Station", "onboard_crew": 1,
               "active_expeditions": [{"name": "Expedition 75", "crew": [
                 {"id": 10, "role": {"role": "Flight Engineer"}, "astronaut": {"id": 1, "name": "Jessica Watkins"}}]}]},
              {"id": 18, "name": "Tiangong space station",
               "active_expeditions": [{"name": "Shenzhou 23", "crew": [
                 {"id": 11, "role": {"role": "Commander"}, "astronaut": {"id": 2, "name": "Zhu Yangzhu"}}]}]}
            ]}
        """.trimIndent()
    }
}
