package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.native.concurrent.SharedImmutable

/**
 * The default [CoroutineScope] used if no specific scope is provided.
 */
@SharedImmutable
internal val defaultCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)