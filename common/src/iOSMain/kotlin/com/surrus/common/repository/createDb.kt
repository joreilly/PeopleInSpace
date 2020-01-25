package com.surrus.common.repository

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase

actual fun createDb(): PeopleInSpaceDatabase {
    val driver = NativeSqliteDriver(PeopleInSpaceDatabase.Schema, "peopleinspace.db")
    return PeopleInSpaceDatabase(driver)
}