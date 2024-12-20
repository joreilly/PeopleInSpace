package com.surrus.common.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.*
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
actual class NativeModule actual constructor(){
    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = Java.create()
}


@Single
actual class PeopleInSpaceDatabaseWrapper() {
    actual fun database(): PeopleInSpaceDatabase?
            = PeopleInSpaceDatabase(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY))
}
