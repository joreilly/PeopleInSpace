package com.surrus.common.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import com.surrus.common.di.IDatabaseDependencyProvider
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase


actual fun createDb(dependencyProvider: IDatabaseDependencyProvider): PeopleInSpaceDatabase? {
    return null
}

actual fun getLogger(): Logger = CommonLogger()