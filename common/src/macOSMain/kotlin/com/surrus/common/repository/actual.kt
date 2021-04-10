package com.surrus.common.repository

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NSLogLogger
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.surrus.common.di.IDatabaseDependencyProvider
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase

actual fun createDb(dependencyProvider: IDatabaseDependencyProvider): PeopleInSpaceDatabase? {
    val driver = NativeSqliteDriver(PeopleInSpaceDatabase.Schema, "peopleinspace.db")
    return PeopleInSpaceDatabase(driver)
}

actual fun getLogger(): Logger = NSLogLogger()