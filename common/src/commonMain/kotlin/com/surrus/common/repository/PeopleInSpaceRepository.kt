package com.surrus.common.repository

import co.touchlab.kermit.Kermit
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface PeopleInSpaceRepositoryInterface {
    fun fetchPeopleAsFlow(): Flow<List<Assignment>>
    fun pollISSPosition(): Flow<IssPosition>
    suspend fun fetchPeople(): List<Assignment>
    suspend fun fetchAndStorePeople()
}

class PeopleInSpaceRepository : KoinComponent, PeopleInSpaceRepositoryInterface {
    private val peopleInSpaceApi: PeopleInSpaceApi by inject()
    private val logger: Kermit by inject()

    @NativeCoroutineScope
    private val coroutineScope: CoroutineScope = MainScope()
    private val peopleInSpaceDatabase: PeopleInSpaceDatabaseWrapper by inject()
    private val peopleInSpaceQueries = peopleInSpaceDatabase.instance?.peopleInSpaceQueries

    init {
        coroutineScope.launch {
            fetchAndStorePeople()
        }
    }

    override fun fetchPeopleAsFlow(): Flow<List<Assignment>> {
        // the main reason we need to do this check is that sqldelight isn't currently
        // setup for javascript client
        return peopleInSpaceQueries?.selectAll(
            mapper = { name, craft, personImageUrl, personBio ->
                Assignment(name = name, craft = craft, personImageUrl = personImageUrl, personBio = personBio)
            }
        )?.asFlow()?.mapToList() ?: flowOf(emptyList())
    }

    override suspend fun fetchAndStorePeople() {
        logger.d { "fetchAndStorePeople" }
        try {
            val result = peopleInSpaceApi.fetchPeople()

            // this is very basic implementation for now that removes all existing rows
            // in db and then inserts results from api request
            // using "transaction" accelerate the batch of queries, especially inserting
            peopleInSpaceQueries?.transaction {
                peopleInSpaceQueries.deleteAll()
                result.people.forEach {
                    peopleInSpaceQueries.insertItem(
                        it.name,
                        it.craft,
                        it.personImageUrl,
                        it.personBio
                    )
                }
            }
        } catch (e: Exception) {
            // TODO report error up to UI
            logger.w(e) { "Exception during fetchAndStorePeople: $e" }
        }
    }

    // Used by web and apple clients atm
    override suspend fun fetchPeople(): List<Assignment> = peopleInSpaceApi.fetchPeople().people

    override fun pollISSPosition(): Flow<IssPosition> {
        // The returned will be frozen in Kotlin Native. We can't freeze the Koin internals
        // so we'll use local variables to prevent the Koin internals from freezing.
        val api = peopleInSpaceApi
        val logger = logger
        return flow {
            while (true) {
                val position = api.fetchISSPosition().iss_position
                emit(position)
                logger.d("PeopleInSpaceRepository") { position.toString() }
                delay(POLL_INTERVAL)
            }
        }
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}
