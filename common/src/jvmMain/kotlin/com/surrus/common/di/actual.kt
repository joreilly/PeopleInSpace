//package com.surrus.common.di
//
//import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
//import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
//import io.ktor.client.engine.HttpClientEngine
//import io.ktor.client.engine.java.*
//import org.koin.core.annotation.ComponentScan
//import org.koin.core.annotation.Module
//import org.koin.core.annotation.Single
//
//
//actual fun platformModule() = module {
//    single {
//        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
//            .also { PeopleInSpaceDatabase.Schema.create(it) }
//        PeopleInSpaceDatabaseWrapper(PeopleInSpaceDatabase(driver))
//    }
//    single { Java.create() }
//}
