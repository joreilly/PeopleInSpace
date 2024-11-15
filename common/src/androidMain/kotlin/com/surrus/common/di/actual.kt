//package com.surrus.common.di
//
//import app.cash.sqldelight.driver.android.AndroidSqliteDriver
//import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
//import io.ktor.client.engine.HttpClientEngine
//import io.ktor.client.engine.android.*
//import org.koin.core.annotation.ComponentScan
//import org.koin.core.annotation.Module
//import org.koin.dsl.module
//
//
//@Module
//@ComponentScan("com.surrus.common")
//
//actual class NativeModule
//
//
//actual fun platformModule() = module {
//    single {
//        val driver =
//            AndroidSqliteDriver(PeopleInSpaceDatabase.Schema, get(), "peopleinspace.db")
//
//        PeopleInSpaceDatabaseWrapper(PeopleInSpaceDatabase(driver))
//    }
//    single<HttpClientEngine> { Android.create() }
//}
