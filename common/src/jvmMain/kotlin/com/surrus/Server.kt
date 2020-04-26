package com.surrus

import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.*
import org.litote.kmongo.async.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.async.getCollection
import com.mongodb.ConnectionString
import com.surrus.common.repository.PeopleInSpaceRepository
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.xml.bind.JAXBElement

fun main() {
    val repository = PeopleInSpaceRepository()

    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/people") {
                repository.fetchPeopleAsFlow().collect {
                    call.respond(it)
                }
            }
        }
    }.start(wait = true)
}