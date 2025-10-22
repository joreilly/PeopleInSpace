package dev.johnoreilly.common.di

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import dev.johnoreilly.common.viewmodel.PersonListViewModel
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

actual class ContextWrapper(val context: Context)

@Module
actual class ViewModelModule {

    @KoinViewModel
    fun personListViewModel() = PersonListViewModel()

    @KoinViewModel
    fun iSSPositionViewModel() = ISSPositionViewModel()
}

@Module
actual class NativeModule {

    @Single
    actual fun providesContextWrapper(scope : Scope) : ContextWrapper = ContextWrapper(scope.get())

    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = Android.create()


    @Single
    actual fun getPeopleInSpaceDatabaseWrapper(ctx : ContextWrapper): PeopleInSpaceDatabaseWrapper {
        val driver = AndroidSqliteDriver(PeopleInSpaceDatabase.Schema.synchronous(), ctx.context, "peopleinspace.db")
        return PeopleInSpaceDatabaseWrapper(driver, PeopleInSpaceDatabase(driver))
    }

}

