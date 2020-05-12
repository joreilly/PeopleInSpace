package com.surrus.peopleinspace

import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PeopleInSpaceTest {
    @Test
    fun testGetPeople() = runBlocking {
        val peopleInSpaceApi = PeopleInSpaceApi()
        val result = peopleInSpaceApi.fetchPeople()
        println(result)
        assert(result.people.isNotEmpty())
    }
}
