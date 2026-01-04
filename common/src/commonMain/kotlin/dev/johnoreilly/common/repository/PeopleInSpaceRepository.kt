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
) : PeopleInSpaceRepositoryInterface {

    val coroutineScope: CoroutineScope = MainScope()
    private val peopleInSpaceQueries = peopleInSpaceDatabase.instance.peopleInSpaceQueries

    val logger = Logger.withTag("PeopleInSpaceRepository")

    init {
        coroutineScope.launch {
            // TODO figure out cleaner place to invoke this (needed for web implementatin)
            PeopleInSpaceDatabase.Schema.awaitCreate(peopleInSpaceDatabase.driver)
            fetchAndStorePeople()
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
            // TODO report error up to UI
            logger.w(e) { "Exception during fetchAndStorePeople: $e" }
        }
    }

    override suspend fun fetchISSFuturePosition(): List<OrbitPoint> {
        return astroviewerApi.fetchISSFuturePositions().orbitData
    }

    override fun pollISSPosition(): Flow<IssPosition> {
        return flow {
            while (true) {
                try {
                    val position = peopleInSpaceApi.fetchISSPosition().iss_position
                    if (currentCoroutineContext().isActive) {
                        emit(position)
                    }
                    logger.d { position.toString() }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // TODO report error up to UI
                    logger.w(e) { "Exception during pollISSPosition: $e" }
                }
                delay(POLL_INTERVAL)
            }
        }
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}
