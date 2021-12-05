package com.surrus.common.repository

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.ScanRequest
import co.touchlab.kermit.Logger
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

    @NativeCoroutineScope
    private val coroutineScope: CoroutineScope = MainScope()
    private val peopleInSpaceDatabase: PeopleInSpaceDatabaseWrapper by inject()
    private val peopleInSpaceQueries = peopleInSpaceDatabase.instance?.peopleInSpaceQueries

    val logger = Logger.withTag("PeopleInSpaceRepository")

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
    //override suspend fun fetchPeople(): List<Assignment> = peopleInSpaceApi.fetchPeople().people


    val staticCredentials = StaticCredentialsProvider {
        accessKeyId = ""
        secretAccessKey = ""
    }

    val dynamoDbClient = DynamoDbClient{
        region = "eu-west-1"
        credentialsProvider = staticCredentials
    }

    val dynamocDbTable = "PeopleInSpace"

    override suspend fun fetchPeople(): List<Assignment> {
        val scanRequest = ScanRequest {
            tableName = dynamocDbTable
        }

        val people = mutableListOf<Assignment>()
        val result = dynamoDbClient.scan(scanRequest)
        result.items?.forEach { item ->
            val id = (item["id"] as AttributeValue.S).value
            val craft = (item["craft"] as AttributeValue.S).value
            val personImageUrl = (item["personImageUrl"] as AttributeValue.S).value
            val person = Assignment(id, craft, personImageUrl)
            people.add(person)
        }

        return people
    }

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
