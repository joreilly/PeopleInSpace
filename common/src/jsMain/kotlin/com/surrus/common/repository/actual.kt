package com.surrus.common.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


actual fun createDb(): PeopleInSpaceDatabase? {
    return null
}

actual fun getLogger(): Logger = CommonLogger()