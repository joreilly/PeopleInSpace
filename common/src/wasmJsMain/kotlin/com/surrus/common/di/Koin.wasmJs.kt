package dev.johnoreilly.common.di

import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase.Companion.invoke
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.dsl.module

private class ContextWrapper

actual fun nativeModule() = module {
    includes(viewModelsModule())
    single { ContextWrapper() }
    single<HttpClientEngine> { Js.create() }
    single {
        val driver = createDefaultWebWorkerDriver()
        PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }
}
