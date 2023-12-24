package com.surrus

import com.surrus.common.di.initKoin
import com.surrus.common.remote.PeopleInSpaceApi

suspend fun main() {
    val koin = initKoin(enableNetworkLogs = true).koin
    val api = koin.get<PeopleInSpaceApi>()
    println(api.fetchPeople())
}
