package dev.johnoreilly.common.di

import app.cash.sqldelight.db.SqlDriver
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase

public class PeopleInSpaceDatabaseWrapper(
    public val driver: SqlDriver,
    public val instance: PeopleInSpaceDatabase,
)
