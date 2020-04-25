package com.surrus

import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val api = PeopleInSpaceApi()
    val people = api.fetchPeople()
    println(people)
}