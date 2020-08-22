package com.surrus.common.repository

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


lateinit var appContext: Context

actual fun createDb(): PeopleInSpaceDatabase? {
    val driver = AndroidSqliteDriver(PeopleInSpaceDatabase.Schema, appContext, "peopleinspace.db")
    return PeopleInSpaceDatabase(driver)
}
