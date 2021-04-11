package com.surrus.common.repository

import co.touchlab.kermit.LogcatLogger
import co.touchlab.kermit.Logger
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver =
            AndroidSqliteDriver(PeopleInSpaceDatabase.Schema, get(), "peopleinspace.db")

        PeopleInSpaceDatabaseWrapper(PeopleInSpaceDatabase(driver))
    }
}

actual fun getLogger(): Logger = LogcatLogger()