//
//  NativeFlow.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A function that collects a Kotlin coroutines Flow via callbacks.
///
/// The function takes an `onItem`, `onComplete` and `onCancelled` callback
/// and returns a cancellable that can be used to cancel the collection.
public typealias NativeFlow<Output, Failure: Error, Unit> = (
    _ onItem: @escaping NativeCallback2<Output, () -> Unit, Unit>,
    _ onComplete: @escaping NativeCallback<Failure?, Unit>,
    _ onCancelled: @escaping NativeCallback<Failure, Unit>
) -> NativeCancellable<Unit>
