package com.surrus.common.repository

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.*
import org.koin.dsl.module



actual fun platformModule() = module {
    single {
        val driver =
            AndroidSqliteDriver(
                schema = PeopleInSpaceDatabase.Schema.synchronous(),
                context = get(),
                name = "peopleinspace.db")

        PeopleInSpaceDatabaseWrapper(PeopleInSpaceDatabase(driver))
    }
    single<HttpClientEngine> { Android.create() }
}
