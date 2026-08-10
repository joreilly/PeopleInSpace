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
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/** The one platform difference between the Windows and macOS clients. */
internal expect fun createPlatformHttpClientEngine(): HttpClientEngine

/**
 * Self-contained entry point for the shared data layer, exported through the NuGet package.
 *
 * The Windows and macOS builds deliberately share this class so both native assets sit behind one
 * managed surface. [storageDirectory] must identify an existing directory writable by the caller.
 * The client does not initialise Koin or expose AndroidX types; it owns its HTTP client, SQLite
 * driver and coroutine scope instead.
 */
class PeopleInSpaceClient(storageDirectory: String) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val engine = createPlatformHttpClientEngine()
    private val httpClient: HttpClient = createHttpClient(
        httpClientEngine = engine,
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
    private val snapshotReader = PeopleInSpaceSnapshotReader(peopleState, issState)

    /** Requests a fresh people-list synchronisation. */
    suspend fun refresh() {
        controller.refresh()
    }

    /** Starts a refresh without requiring a managed-to-native async callback. */
    fun requestRefresh() {
        scope.launch { controller.refresh() }
    }

    /** AOT-safe scalar projection used by managed hosts that cannot reflectively create wrappers. */
    fun capturePeople() = snapshotReader.capturePeople()
    fun capturedPersonName(index: Int) = snapshotReader.capturedPersonName(index)
    fun capturedPersonCraft(index: Int) = snapshotReader.capturedPersonCraft(index)
    fun capturedPersonNationality(index: Int) = snapshotReader.capturedPersonNationality(index)
    fun capturedPersonImageUrl(index: Int) = snapshotReader.capturedPersonImageUrl(index)
    fun capturedPersonBio(index: Int) = snapshotReader.capturedPersonBio(index)
    fun capturedPeopleInitialLoading() = snapshotReader.capturedPeopleInitialLoading()
    fun capturedPeopleRefreshing() = snapshotReader.capturedPeopleRefreshing()
    fun capturedPeopleErrorMessage() = snapshotReader.capturedPeopleErrorMessage()
    fun captureIss() = snapshotReader.captureIss()
    fun capturedIssLatitude() = snapshotReader.capturedIssLatitude()
    fun capturedIssLongitude() = snapshotReader.capturedIssLongitude()
    fun capturedIssTimestamp() = snapshotReader.capturedIssTimestamp()
    fun capturedIssHasPosition() = snapshotReader.capturedIssHasPosition()
    fun capturedIssLoading() = snapshotReader.capturedIssLoading()
    fun capturedIssErrorMessage() = snapshotReader.capturedIssErrorMessage()

    /** Releases all resources owned by this client. Safe to call more than once. */
    fun close() {
        controller.close()
    }

    private fun databaseDirectory(storageDirectory: String): String {
        require(storageDirectory.isNotBlank()) { "storageDirectory must not be blank" }
        return storageDirectory.trimEnd('/', '\\')
    }
}
