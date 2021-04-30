package com.surrus.common.repository

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NSLogLogger
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver = NativeSqliteDriver(PeopleInSpaceDatabase.Schema, "peopleinspace.db")
        PeopleInSpaceDatabaseWrapper(PeopleInSpaceDatabase(driver))
    }
    single<Logger>{ NSLogLogger() }
}

