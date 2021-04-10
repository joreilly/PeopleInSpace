package com.surrus

import com.surrus.common.di.createDbClient
import com.surrus.common.di.initKoin
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

fun main() {
    runBlocking {
        val koin = initKoin(enableNetworkLogs = true){
            modules(module {
                single {  createDbClient() }
            })
        }.koin
        val api = koin.get<PeopleInSpaceApi>()
        println(api.fetchPeople())
    }
}
