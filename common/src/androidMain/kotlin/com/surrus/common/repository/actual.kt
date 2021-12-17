package com.surrus.common.repository

import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.android.*
import org.koin.dsl.module



actual fun platformModule() = module {
    single {
        val driver =
            AndroidSqliteDriver(PeopleInSpaceDatabase.Schema, get(), "peopleinspace.db")

        PeopleInSpaceDatabaseWrapper(PeopleInSpaceDatabase(driver))
    }
    single { Android.create() }
}
