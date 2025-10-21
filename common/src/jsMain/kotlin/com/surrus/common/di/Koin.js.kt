package com.surrus.common.di

import com.surrus.common.viewmodel.ISSPositionViewModel
import com.surrus.common.viewmodel.PersonListViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
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
    actual fun getHttpClientEngine(): HttpClientEngine = Js.create()

    @Single
    actual fun getPeopleInSpaceDatabaseWrapper(ctx : ContextWrapper) : PeopleInSpaceDatabaseWrapper = PeopleInSpaceDatabaseWrapper()
}
