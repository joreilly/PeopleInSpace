package com.surrus.peopleinspace

import com.surrus.common.di.initKoin
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class PeopleInSpaceTest {
    @Test
    fun testGetPeople() = runBlocking {
        val koin = initKoin(enableNetworkLogs = true).koin
        val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()
        val result = peopleInSpaceApi.fetchPeople()
        println(result)
        assertTrue(result.people.isNotEmpty())
    }
}
