package dev.johnoreilly.common.repository

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import dev.johnoreilly.common.di.PeopleInSpaceDatabaseWrapper
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.AstroviewerApi
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.remote.OrbitPoint
import dev.johnoreilly.common.remote.PeopleInSpaceApi
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.annotation.Single


interface PeopleInSpaceRepositoryInterface {
    // false until the first network fetch has finished (successfully or not),
    // letting the UI distinguish "not fetched yet" from a genuinely empty result
    val initialSyncCompleted: StateFlow<Boolean>

    /** True while the people list is being synchronised with the service. */
    val peopleSyncLoading: StateFlow<Boolean>

    /** The most recent people-list synchronisation failure, if any. */
    val peopleSyncError: StateFlow<Throwable?>

    /** True while an ISS position request is in progress. */
    val issPollLoading: StateFlow<Boolean>

    /** The most recent ISS polling failure, if any. */
    val issPollError: StateFlow<Throwable?>

    fun fetchPeopleAsFlow(): Flow<List<Assignment>>
    fun pollISSPosition(): Flow<IssPosition>
    suspend fun fetchISSFuturePosition(): List<OrbitPoint>
    suspend fun fetchAndStorePeople()
}

@Single
class PeopleInSpaceRepository(
    private val peopleInSpaceApi: PeopleInSpaceApi,
    private val peopleInSpaceDatabase: PeopleInSpaceDatabaseWrapper,
    private val astroviewerApi: AstroviewerApi,
    val coroutineScope: CoroutineScope,
) : PeopleInSpaceRepositoryInterface {

    private val peopleInSpaceQueries = peopleInSpaceDatabase.instance.peopleInSpaceQueries

    val logger = Logger.withTag("PeopleInSpaceRepository")

    private val _initialSyncCompleted = MutableStateFlow(false)
    override val initialSyncCompleted: StateFlow<Boolean> = _initialSyncCompleted.asStateFlow()

    private val _peopleSyncLoading = MutableStateFlow(false)
    override val peopleSyncLoading: StateFlow<Boolean> = _peopleSyncLoading.asStateFlow()

    private val _peopleSyncError = MutableStateFlow<Throwable?>(null)
    override val peopleSyncError: StateFlow<Throwable?> = _peopleSyncError.asStateFlow()

    private val _issPollLoading = MutableStateFlow(false)
    override val issPollLoading: StateFlow<Boolean> = _issPollLoading.asStateFlow()

    private val _issPollError = MutableStateFlow<Throwable?>(null)
    override val issPollError: StateFlow<Throwable?> = _issPollError.asStateFlow()

    init {
        coroutineScope.launch {
            try {
                // TODO figure out cleaner place to invoke this (needed for web implementatin)
                PeopleInSpaceDatabase.Schema.awaitCreate(peopleInSpaceDatabase.driver)
                fetchAndStorePeople()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _peopleSyncError.value = e
                logger.w(e) { "Exception while creating PeopleInSpace database: $e" }
            } finally {
                _initialSyncCompleted.value = true
            }
        }
    }

    override fun fetchPeopleAsFlow(): Flow<List<Assignment>> {
        return peopleInSpaceQueries.selectAll(
            mapper = { name, craft, personImageUrl, personBio, nationality ->
                Assignment(
                    name = name,
                    craft = craft,
                    personImageUrl = personImageUrl,
                    personBio = personBio,
                    nationality = nationality
                )
            }
        ).asFlow().mapToList(Dispatchers.Default)
    }

    override suspend fun fetchAndStorePeople() {
        logger.d { "fetchAndStorePeople" }
        _peopleSyncLoading.value = true
        _peopleSyncError.value = null
        try {
            val result = peopleInSpaceApi.fetchPeople()

            // this is very basic implementation for now that removes all existing rows
            // in db and then inserts results from api request
            peopleInSpaceQueries.transaction {
                peopleInSpaceQueries.deleteAll()
                result.people.forEach {
                    peopleInSpaceQueries.insertItem(
                        it.name,
                        it.craft,
                        it.personImageUrl,
                        it.personBio,
                        it.nationality
                    )
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _peopleSyncError.value = e
            logger.w(e) { "Exception during fetchAndStorePeople: $e" }
        } finally {
            _peopleSyncLoading.value = false
        }
    }

    override suspend fun fetchISSFuturePosition(): List<OrbitPoint> {
        return astroviewerApi.fetchISSFuturePositions().orbitData
    }

    override fun pollISSPosition(): Flow<IssPosition> {
        return issPositionPollingFlow(
            pollIntervalMillis = POLL_INTERVAL,
            fetchPosition = { peopleInSpaceApi.fetchISSPosition().iss_position },
            onLoadingChanged = { _issPollLoading.value = it },
            onError = { error ->
                _issPollError.value = error
                if (error != null) {
                    logger.w(error) { "Exception during pollISSPosition: $error" }
                }
            },
        ).onEach { position -> logger.d { position.toString() } }
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}

/**
 * Retries an ISS position request indefinitely. Keeping the polling mechanics
 * separate from the repository makes the retry and observable loading/error
 * contract deterministic to test without a network client.
 */
internal fun issPositionPollingFlow(
    pollIntervalMillis: Long,
    fetchPosition: suspend () -> IssPosition,
    onLoadingChanged: (Boolean) -> Unit,
    onError: (Throwable?) -> Unit,
): Flow<IssPosition> = flow {
    require(pollIntervalMillis >= 0) { "pollIntervalMillis must not be negative" }
    while (true) {
        onLoadingChanged(true)
        onError(null)
        try {
            emit(fetchPosition())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError(e)
        } finally {
            onLoadingChanged(false)
        }
        delay(pollIntervalMillis)
    }
}
