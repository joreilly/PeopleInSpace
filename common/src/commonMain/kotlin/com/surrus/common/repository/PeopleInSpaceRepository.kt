package com.surrus.common.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent
import org.koin.core.inject

expect fun createDb() : PeopleInSpaceDatabase?


val personImages = mapOf(
    "Chris Cassidy" to "https://www.nasa.gov/sites/default/files/styles/side_image/public/thumbnails/image/9368855148_f79942efb7_o.jpg?itok=-w5yoryN",
    "Anatoly Ivanishin" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Anatoli_Ivanishin_2011.jpg/440px-Anatoli_Ivanishin_2011.jpg",
    "Ivan Vagner" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Ivan_Vagner_%28Jsc2020e014992%29.jpg/440px-Ivan_Vagner_%28Jsc2020e014992%29.jpg",
)

val personBios = mapOf(
    "Chris Cassidy" to "Christopher John \"Chris\" Cassidy (born January 4, 1970, in Salem, Massachusetts) is a NASA astronaut and United States Navy SEAL. Chris Cassidy achieved the rank of captain in the U.S. Navy. He was the Chief of the Astronaut Office at NASA from July 2015 until June 2017.",
    "Anatoly Ivanishin" to "Anatoli Alekseyevich Ivanishin (Russian: Анатолий Алексеевич Иванишин; born 15 January 1969) is a Russian cosmonaut. His first visit to space was to the International Space Station on board the Soyuz TMA-22 spacecraft as an Expedition 29 / Expedition 30 crew member, launching in November 2011 and returning in April 2012. Ivanishin was the Commander of the International Space Station for Expedition 49.",
    "Ivan Vagner" to "Ivan Viktorovich Vagner (born 10 July 1985) is a Russian engineer and cosmonaut who was selected in October 2010. He graduated from the Baltic State Technical University in 2008, before working as an engineer for RKK Energia.\n\nHe began his first spaceflight in April 2020 as a Flight Engineer on Soyuz MS-16 and Expedition 62/63.",
)


class PeopleInSpaceRepository() : KoinComponent {
    private val peopleInSpaceApi: PeopleInSpaceApi by inject()
    private val peopleInSpaceDatabase = createDb()
    private val peopleInSpaceQueries = peopleInSpaceDatabase?.peopleInSpaceQueries

    init {
        GlobalScope.launch(Dispatchers.Main) {
            fetchAndStorePeople()
        }
    }

    fun fetchPeopleAsFlow()  = peopleInSpaceQueries?.selectAll(mapper = { name, craft ->
            Assignment(name = name, craft = craft)
        })?.asFlow()?.mapToList()

    private suspend fun fetchAndStorePeople()  {
        val result = peopleInSpaceApi.fetchPeople()

        // this is very basic implementation for now that removes all existing rows
        // in db and then inserts results from api request
        peopleInSpaceQueries?.deleteAll()
        result.people.forEach {
            peopleInSpaceQueries?.insertItem(it.name, it.craft)
        }
    }

    fun getPersonBio(personName: String): String {
        return personBios[personName] ?: ""
    }

    // called from Kotlin/Native clients
    fun fetchPeople(success: (List<Assignment>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            fetchPeopleAsFlow()?.collect {
                success(it)
            }
        }
    }

    @Throws(Exception::class)
    suspend fun fetchISSPosition() = peopleInSpaceApi.fetchISSPosition().iss_position
}

