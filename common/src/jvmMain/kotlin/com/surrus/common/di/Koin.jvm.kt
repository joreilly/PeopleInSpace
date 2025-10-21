package com.surrus.common.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.surrus.common.viewmodel.ISSPositionViewModel
import com.surrus.common.viewmodel.PersonListViewModel
import com.surrus.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.*
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
actual class NativeModule {

    @Single
    actual fun providesContextWrapper(scope : Scope) : ContextWrapper = ContextWrapper()

    @Single
    actual fun getHttpClientEngine(): HttpClientEngine = Java.create()


    @Single
    actual fun getPeopleInSpaceDatabaseWrapper(ctx : ContextWrapper) : PeopleInSpaceDatabaseWrapper = PeopleInSpaceDatabaseWrapper(
        PeopleInSpaceDatabase(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY))
    )

}