package com.surrus.common.repository

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase


actual fun createDb(): PeopleInSpaceDatabase? {
    val driver =  JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        .also { PeopleInSpaceDatabase.Schema.create(it) }
    return PeopleInSpaceDatabase(driver)
}
