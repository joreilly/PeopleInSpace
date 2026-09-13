package dev.johnoreilly.common.di

import app.cash.sqldelight.db.SqlDriver
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase

class PeopleInSpaceDatabaseWrapper(val driver: SqlDriver, val instance: PeopleInSpaceDatabase)
