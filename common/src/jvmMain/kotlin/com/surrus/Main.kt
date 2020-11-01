package com.surrus

import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.common.repository.PeopleInSpaceRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

fun main()  {
    runBlocking {
        val api = PeopleInSpaceApi()
        println(api.fetchPeople())

    }
}