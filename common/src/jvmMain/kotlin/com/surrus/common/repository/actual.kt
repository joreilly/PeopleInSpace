package dev.johnoreilly.common.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.johnoreilly.common.di.PeopleInSpaceDatabaseWrapper
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.java.*
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            .also { PeopleInSpaceDatabase.Schema.create(it) }
        PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }
    single { Java.create() }
}
