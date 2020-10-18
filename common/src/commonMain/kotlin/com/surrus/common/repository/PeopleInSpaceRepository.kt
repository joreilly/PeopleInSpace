package com.surrus.common.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.surrus.common.model.personBios
import com.surrus.common.model.personImages
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent
import org.koin.core.inject

expect fun createDb() : PeopleInSpaceDatabase?


class PeopleInSpaceRepository() : KoinComponent {
    private val peopleInSpaceApi: PeopleInSpaceApi by inject()
    private val peopleInSpaceDatabase = createDb()
    private val peopleInSpaceQueries = peopleInSpaceDatabase?.peopleInSpaceQueries

    init {
        GlobalScope.launch(Dispatchers.Main) {
            fetchAndStorePeople()
        }
    }

    fun fetchPeopleAsFlow()  = peopleInSpaceQueries?.selectAll(mapper = { name, craft ->
            Assignment(name = name, craft = craft)
        })?.asFlow()?.mapToList()

    private suspend fun fetchAndStorePeople()  {
        val result = peopleInSpaceApi.fetchPeople()

        // this is very basic implementation for now that removes all existing rows
        // in db and then inserts results from api request
        peopleInSpaceQueries?.deleteAll()
        result.people.forEach {
            peopleInSpaceQueries?.insertItem(it.name, it.craft)
        }
    }

    // Used by web client atm
    suspend fun fetchPeople() = peopleInSpaceApi.fetchPeople().people

    fun getPersonBio(personName: String): String {
        return personBios[personName] ?: ""
    }

    fun getPersonImage(personName: String): String {
        return personImages[personName] ?: ""
    }

    // called from Kotlin/Native clients
    fun fetchPeople(success: (List<Assignment>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            fetchPeopleAsFlow()?.collect {
                success(it)
            }
        }
    }

    @Throws(Exception::class)
    suspend fun fetchISSPosition() = peopleInSpaceApi.fetchISSPosition().iss_position
}

