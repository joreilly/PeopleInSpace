package dev.johnoreilly

import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.remote.PeopleInSpaceApi

suspend fun main() {
    val koin = initKoin(enableNetworkLogs = true).koin
    val api = koin.get<PeopleInSpaceApi>()
    println(api.fetchPeople())
}
