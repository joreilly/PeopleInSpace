package com.surrus.common.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.surrus.common")
actual class NativeModule actual constructor(){
    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = Darwin.create()
}


@Single
actual class PeopleInSpaceDatabaseWrapper() {
    actual fun database(): PeopleInSpaceDatabase?
            = PeopleInSpaceDatabase(NativeSqliteDriver(PeopleInSpaceDatabase.Schema, "peopleinspace.db"))
}
