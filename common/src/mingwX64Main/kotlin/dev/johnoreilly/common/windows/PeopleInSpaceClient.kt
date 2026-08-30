package dev.johnoreilly.common.windows

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.johnoreilly.common.di.PeopleInSpaceDatabaseWrapper
import dev.johnoreilly.common.di.createHttpClient
import dev.johnoreilly.common.remote.AstroviewerApi
import dev.johnoreilly.common.remote.PeopleInSpaceApi
import dev.johnoreilly.common.repository.PeopleInSpaceRepository
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.winhttp.WinHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json

/**
 * Self-contained entry point for the shared data layer, exported through the NuGet package.
 *
 * [storageDirectory] must identify an existing directory writable by the caller. The client does
 * not initialise Koin or expose AndroidX types; it owns its HTTP client, SQLite driver and
 * coroutine scope instead.
 */
class PeopleInSpaceClient(storageDirectory: String) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val httpClient: HttpClient = createHttpClient(
        httpClientEngine = WinHttp.create(),
        json = Json { isLenient = true; ignoreUnknownKeys = true },
        enableNetworkLogs = false,
    )
    private val driver = NativeSqliteDriver(
        schema = PeopleInSpaceDatabase.Schema.synchronous(),
        name = "peopleinspace.db",
        onConfiguration = { configuration ->
            configuration.copy(
                extendedConfig = configuration.extendedConfig.copy(
                    basePath = databaseDirectory(storageDirectory),
                ),
            )
        },
    )
    private val repository = PeopleInSpaceRepository(
        peopleInSpaceApi = PeopleInSpaceApi(httpClient),
        peopleInSpaceDatabase = PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver)),
        astroviewerApi = AstroviewerApi(httpClient),
        coroutineScope = scope,
    )
    private val controller = PeopleInSpaceClientController(repository, scope) {
        httpClient.close()
        driver.close()
    }

    /** Continuously updated people data, loading state, and latest sync error. */
    val peopleState: StateFlow<PeopleState> = controller.peopleState

    /** Continuously updated ISS data, including errors from the retrying poller. */
    val issState: StateFlow<IssState> = controller.issState

    /** Requests a fresh people-list synchronisation. */
    suspend fun refresh() {
        controller.refresh()
    }

    /** Releases all resources owned by this client. Safe to call more than once. */
    fun close() {
        controller.close()
    }

    private fun databaseDirectory(storageDirectory: String): String {
        require(storageDirectory.isNotBlank()) { "storageDirectory must not be blank" }
        return storageDirectory.trimEnd('/', '\\')
    }
}
