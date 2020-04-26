package com.surrus

import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.common.repository.PeopleInSpaceRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

@InternalCoroutinesApi
fun main()  {
    runBlocking {
        val api = PeopleInSpaceApi()
        println(api.fetchPeople())

        val repository = PeopleInSpaceRepository()
        repository.fetchPeopleAsFlow()?.collect {
            println(it)
        }
    }
}