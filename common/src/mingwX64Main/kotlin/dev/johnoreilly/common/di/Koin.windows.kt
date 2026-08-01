package dev.johnoreilly.common.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.winhttp.WinHttp
import org.koin.dsl.module

private class ContextWrapper

actual fun nativeModule() = module {
    single { ContextWrapper() }
    single<HttpClientEngine> { WinHttp.create() }
    single {
        val driver = NativeSqliteDriver(PeopleInSpaceDatabase.Schema.synchronous(), "peopleinspace.db")
        PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }
}
