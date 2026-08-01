package dev.johnoreilly.common.di

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.dsl.module

private class ContextWrapper(val context: Context)

actual fun nativeModule() = module {
    includes(viewModelsModule())
    single { ContextWrapper(get()) }
    single<HttpClientEngine> { Android.create() }
    single {
        val ctx: ContextWrapper = get()
        val driver = AndroidSqliteDriver(PeopleInSpaceDatabase.Schema.synchronous(), ctx.context, "peopleinspace.db")
        PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }
}
