package dev.johnoreilly.common.windows

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.johnoreilly.common.di.PeopleInSpaceDatabaseWrapper
import dev.johnoreilly.common.di.createHttpClient
import dev.johnoreilly.common.remote.AstroviewerApi
import dev.johnoreilly.common.remote.PeopleInSpaceApi
import dev.johnoreilly.common.repository.PeopleInSpaceRepository
import dev.johnoreilly.common.viewmodel.IssPositionUiState
import dev.johnoreilly.common.viewmodel.PersonListUiState
import dev.johnoreilly.common.viewmodel.issPositionUiState
import dev.johnoreilly.common.viewmodel.personListUiState
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.winhttp.WinHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

/**
 * Self-contained entry point for the shared data layer, exported through the NuGet package.
 *
 * It projects the same UI state the other clients' ViewModels use (see ExportedState.kt).
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
    private var closed = false

    /** Continuously updated people list state. */
    val peopleState: StateFlow<PeopleState> = repository.personListUiState()
        .map { it.toExported() }
        .stateIn(scope, SharingStarted.Eagerly, PersonListUiState.Loading.toExported())

    /** Continuously updated ISS position state; polling runs for the lifetime of the client. */
    val issState: StateFlow<IssState> = repository.issPositionUiState()
        .map { it.toExported() }
        .stateIn(scope, SharingStarted.Eagerly, IssPositionUiState.Loading.toExported())

    /** Requests a fresh people-list synchronisation. */
    suspend fun refresh() {
        repository.fetchAndStorePeople()
    }

    /** Releases all resources owned by this client. Safe to call more than once. */
    fun close() {
        if (closed) return
        closed = true
        scope.cancel()
        httpClient.close()
        driver.close()
    }

    private fun databaseDirectory(storageDirectory: String): String {
        require(storageDirectory.isNotBlank()) { "storageDirectory must not be blank" }
        return storageDirectory.trimEnd('/', '\\')
    }
}
