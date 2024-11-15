package com.surrus.common.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class NativeModule actual constructor(){
    @Single
    actual fun httpClientEngine(): HttpClientEngine = Js.create()
}