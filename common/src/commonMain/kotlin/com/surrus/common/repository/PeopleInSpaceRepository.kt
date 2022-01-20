package com.surrus.common.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.watch
import com.surrus.common.GetPeopleQuery
import com.surrus.common.IssPositionSubscription
import com.surrus.common.model.Assignment
import com.surrus.common.model.IssPosition
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


interface PeopleInSpaceRepositoryInterface {
    fun fetchPeopleAsFlow(): Flow<List<Assignment>>
    fun pollISSPosition(): Flow<IssPosition>
    suspend fun fetchPeople(): List<Assignment>
    suspend fun fetchAndStorePeople()
}

fun GetPeopleQuery.Person.mapToAssignment() = Assignment(craft, name, personImageUrl, personBio)

class PeopleInSpaceRepository : KoinComponent, PeopleInSpaceRepositoryInterface {
    private val apolloClient: ApolloClient by inject()

    override fun fetchPeopleAsFlow() =
        apolloClient.query(GetPeopleQuery()).watch().map {
            it.dataAssertNoErrors.people.map { it.mapToAssignment() }
        }

    override suspend fun fetchPeople(): List<Assignment> {
        val response = apolloClient.query(GetPeopleQuery()).execute()
        return response.dataAssertNoErrors.people.map {
            it.mapToAssignment()
        }
    }

    override suspend fun fetchAndStorePeople() {
        fetchPeople()
    }

    override fun pollISSPosition() =
        apolloClient.subscription(IssPositionSubscription()).toFlow().map {
            val result = it.dataAssertNoErrors.issPosition
            IssPosition(result.latitude, result.longitude)
        }
}
