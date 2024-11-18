package com.surrus.common.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.surrus.common")
actual class NativeModule actual constructor() {
    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = Android.create()
}


@Single
actual class PeopleInSpaceDatabaseWrapper(val context: Context) {
    actual fun database(): PeopleInSpaceDatabase?
        = PeopleInSpaceDatabase(AndroidSqliteDriver(PeopleInSpaceDatabase.Schema, context, "peopleinspace.db"))
}
