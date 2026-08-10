package dev.johnoreilly.common.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.winhttp.WinHttp
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

actual class ContextWrapper

@Module
actual class NativeModule {

    @Single
    actual fun providesContextWrapper(scope: Scope): ContextWrapper = ContextWrapper()

    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = WinHttp.create()

    @Single
    actual fun getPeopleInSpaceDatabaseWrapper(ctx: ContextWrapper): PeopleInSpaceDatabaseWrapper {
        val driver = NativeSqliteDriver(PeopleInSpaceDatabase.Schema.synchronous(), "peopleinspace.db")
        return PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }
}
