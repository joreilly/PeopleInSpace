package com.surrus.common.di

import app.cash.sqldelight.db.SqlDriver
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase

class PeopleInSpaceDatabaseWrapper(val driver: SqlDriver, val instance: PeopleInSpaceDatabase)
