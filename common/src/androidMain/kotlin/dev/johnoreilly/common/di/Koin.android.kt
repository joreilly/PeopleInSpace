package dev.johnoreilly.common.di

import dev.johnoreilly.common.db.DATABASE_NAME
import dev.johnoreilly.common.db.LEGACY_DATABASE_NAME
import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

internal actual class ContextWrapper(val context: Context)

@Module
internal actual class NativeModule {

    @Single
    actual fun providesContextWrapper(scope : Scope) : ContextWrapper = ContextWrapper(scope.get())

    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = Android.create()


    @Single
    actual fun getPeopleInSpaceDatabaseWrapper(ctx : ContextWrapper): PeopleInSpaceDatabaseWrapper {
        ctx.context.deleteDatabase(LEGACY_DATABASE_NAME)
        val driver = AndroidSqliteDriver(PeopleInSpaceDatabase.Schema.synchronous(), ctx.context, DATABASE_NAME)
        return PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }

}

