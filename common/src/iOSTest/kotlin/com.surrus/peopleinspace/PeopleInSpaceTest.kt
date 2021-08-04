package com.surrus.peopleinspace

import com.surrus.common.di.initKoin
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class PeopleInSpaceTest {
    @Test
    fun testGetPeople() = runBlocking {
        val koin = initKoin(enableNetworkLogs = true).koin
        val repo = koin.get<PeopleInSpaceRepositoryInterface>()
        val result = repo.fetchPeople()
        println(result)
        assertTrue(result.isNotEmpty())
    }
}
