package dev.johnoreilly.common.repository

import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import dev.johnoreilly.common.di.PeopleInSpaceDatabaseWrapper
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver = createDefaultWebWorkerDriver()
        PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }
    single<HttpClientEngine>  { Js.create() }
}
