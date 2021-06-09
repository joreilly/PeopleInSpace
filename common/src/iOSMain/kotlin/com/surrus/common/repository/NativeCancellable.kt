package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.Job
import kotlin.native.concurrent.freeze

/**
 * A function that cancels the coroutines [Job].
 */
typealias NativeCancellable = () -> Unit

/**
 * Creates a [NativeCancellable] for this [Job].
 *
 * The returned cancellable will cancel the job without a cause.
 * @see Job.cancel
 */
internal inline fun Job.asNativeCancellable(): NativeCancellable = { cancel() }.freeze()