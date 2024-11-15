package com.surrus.common.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.*
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module


//@Module
//@ComponentScan("com.surrus.common")
//
//actual class NativeModule

//
//actual fun platformModule() = module {
//    single {
//        val driver = NativeSqliteDriver(PeopleInSpaceDatabase.Schema, "peopleinspace.db")
//        PeopleInSpaceDatabaseWrapper(PeopleInSpaceDatabase(driver))
//    }
//    single<HttpClientEngine> { Darwin.create() }
//}
