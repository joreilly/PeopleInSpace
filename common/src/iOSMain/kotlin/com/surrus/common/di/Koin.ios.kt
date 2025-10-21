package com.surrus.common.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.surrus.common.viewmodel.ISSPositionViewModel
import com.surrus.common.viewmodel.PersonListViewModel
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

actual class ContextWrapper

@Module
actual class ViewModelModule {

    @Factory
    fun personListViewModel() = PersonListViewModel()

    @Factory
    fun iSSPositionViewModel() = ISSPositionViewModel()
}

@Module
actual class NativeModule actual constructor(){

    @Single
    actual fun providesContextWrapper(scope : Scope) : ContextWrapper = ContextWrapper()

    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = Darwin.create()

    @Single
    actual fun getPeopleInSpaceDatabaseWrapper(ctx : ContextWrapper) : PeopleInSpaceDatabaseWrapper =
        PeopleInSpaceDatabaseWrapper(
            PeopleInSpaceDatabase(NativeSqliteDriver(PeopleInSpaceDatabase.Schema, "peopleinspace.db"))
        )
}
