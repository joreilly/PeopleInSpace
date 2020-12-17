package com.surrus.peopleinspace

import com.surrus.common.di.initKoin
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertTrue


class PeopleInSpaceTest {
    @Test
    fun testGetPeople() {
        GlobalScope.launch(Dispatchers.Main) {
            val koin = initKoin(enableNetworkLogs = true).koin
            val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()
            val result = peopleInSpaceApi.fetchPeople()
            println(result)
            assertTrue(result.people.isNotEmpty())
        }
    }
}
