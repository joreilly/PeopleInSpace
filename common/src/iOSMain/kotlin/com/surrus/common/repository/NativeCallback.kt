package com.rickclephas.kmp.nativecoroutines

import kotlin.native.concurrent.freeze

/**
 * A callback with a single argument.
 *
 * We don't want the Swift code to known how to get the [Unit] object so we'll provide it as the second argument.
 * This way Swift can just return the value that it received without knowing what it is/how to get it.
 */
typealias NativeCallback<T> = (T, Unit) -> Unit

/**
 * Invokes the callback with the specified [value].
 */
internal inline operator fun <T> NativeCallback<T>.invoke(value: T) = invoke(value.freeze(), Unit)