package com.surrus.common.repository

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


interface PeopleInSpaceRepositoryInterface {
    fun fetchPeopleAsFlow(): Flow<List<Assignment>>
    fun pollISSPosition(): Flow<IssPosition>
    suspend fun fetchPeople(): List<Assignment>
    suspend fun fetchAndStorePeople()
}

class PeopleInSpaceRepository(
    private val peopleInSpaceApi: PeopleInSpaceApi,
    private val peopleInSpaceDatabase: PeopleInSpaceDatabaseWrapper
) : PeopleInSpaceRepositoryInterface {

    val coroutineScope: CoroutineScope = MainScope()
    private val peopleInSpaceQueries = peopleInSpaceDatabase.instance.peopleInSpaceQueries

    val logger = Logger.withTag("PeopleInSpaceRepository")

    init {
        coroutineScope.launch {
            // TODO figure out cleaner place to invoke this
            PeopleInSpaceDatabase.Schema.awaitCreate(peopleInSpaceDatabase.driver)
            fetchAndStorePeople()
        }
    }

    override fun fetchPeopleAsFlow(): Flow<List<Assignment>> {
        // the main reason we need to do this check is that sqldelight isn't currently
        // setup for javascript client
        return peopleInSpaceQueries?.selectAll(
            mapper = { name, craft, personImageUrl, personBio ->
                Assignment(
                    name = name,
                    craft = craft,
                    personImageUrl = personImageUrl,
                    personBio = personBio
                )
            }
        )?.asFlow()?.mapToList(Dispatchers.Default) ?: flowOf(emptyList())
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
        return flow {
            while (true) {
                val position = peopleInSpaceApi.fetchISSPosition().iss_position
                emit(position)
                logger.d { position.toString() }
                delay(POLL_INTERVAL)
            }
        }
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}
