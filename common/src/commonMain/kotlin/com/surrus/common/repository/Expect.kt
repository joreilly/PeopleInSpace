package com.surrus.common.repository

import co.touchlab.kermit.Logger
import com.surrus.common.di.IDatabaseDependencyProvider
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase

expect fun createDb(dependencyProvider: IDatabaseDependencyProvider) : PeopleInSpaceDatabase?
expect fun getLogger(): Logger