package com.surrus.common.repository

import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PeopleInSpaceRepository {
    private val peopleInSpaceApi = PeopleInSpaceApi()

    suspend fun fetchPeople() : List<Assignment> {
        val result = peopleInSpaceApi.fetchPeople()
        return result.people
    }

    fun fetchPeople(success: (List<Assignment>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            success(fetchPeople())
        }
    }
}
