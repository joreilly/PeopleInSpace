//
//  NativeSuspend.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A function that awaits a suspend function via callbacks.
///
/// The function takes an `onResult`, `onError` and `onCancelled` callback
/// and returns a cancellable that can be used to cancel the suspend function.
public typealias NativeSuspend<Result, Failure: Error, Unit> = (
    _ onResult: @escaping NativeCallback<Result, Unit>,
    _ onError: @escaping NativeCallback<Failure, Unit>,
    _ onCancelled: @escaping NativeCallback<Failure, Unit>
) -> NativeCancellable<Unit>
