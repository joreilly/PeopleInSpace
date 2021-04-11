package com.surrus.common.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import co.touchlab.kermit.NSLogLogger
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

actual fun createDb(): PeopleInSpaceDatabase? {
    val driver = NativeSqliteDriver(PeopleInSpaceDatabase.Schema, "peopleinspace.db")
    return PeopleInSpaceDatabase(driver)
}

actual fun getLogger(): Logger = NSLogLogger()