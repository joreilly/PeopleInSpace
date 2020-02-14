package com.surrus.common.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

expect fun createDb() : PeopleInSpaceDatabase

class PeopleInSpaceRepository {
    private val peopleInSpaceApi = PeopleInSpaceApi()
    private val peopleInSpaceDatabase = createDb()
    private val peopleInSpaceQueries = peopleInSpaceDatabase.peopleInSpaceQueries

    init {
        GlobalScope.launch (Dispatchers.Main) {
            fetchAndStorePeople()
        }
    }

    fun fetchPeopleAsFlow()  = peopleInSpaceQueries.selectAll(mapper = { name, craft ->
            Assignment(name = name, craft = craft)
        }).asFlow().mapToList()

    suspend fun fetchAndStorePeople()  {
        val result = peopleInSpaceApi.fetchPeople()

        // this is very basic implementation for now that removes all existing rows
        // in db and then inserts reults from api request
        peopleInSpaceQueries.deleteAll()
        result.people.forEach {
            peopleInSpaceQueries.insertItem(it.name, it.craft)
        }
    }

    // called from iOS/watchOS client
    fun fetchPeople(success: (List<Assignment>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            fetchPeopleAsFlow().collect {
                success(it)
            }
        }
    }
}
