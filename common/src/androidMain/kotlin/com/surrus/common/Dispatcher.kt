package com.surrus.common

import kotlinx.coroutines.*

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Default