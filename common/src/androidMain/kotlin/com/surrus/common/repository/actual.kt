package com.surrus.common.repository

import android.content.Context
import co.touchlab.kermit.LogcatLogger
import co.touchlab.kermit.Logger
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.surrus.common.di.IDatabaseDependencyProvider
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase


actual fun createDb(dependencyProvider: IDatabaseDependencyProvider): PeopleInSpaceDatabase? {
    return if(dependencyProvider is AndroidDatabaseDependencyProvider) {
        val driver =
            AndroidSqliteDriver(PeopleInSpaceDatabase.Schema, dependencyProvider.context, "peopleinspace.db")
      PeopleInSpaceDatabase(driver)
    } else null
}

actual fun getLogger(): Logger = LogcatLogger()

class AndroidDatabaseDependencyProvider(val context: Context):IDatabaseDependencyProvider