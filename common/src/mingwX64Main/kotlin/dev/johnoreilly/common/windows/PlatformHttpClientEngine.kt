package dev.johnoreilly.common.windows

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.winhttp.WinHttp

internal actual fun createPlatformHttpClientEngine(): HttpClientEngine = WinHttp.create()
