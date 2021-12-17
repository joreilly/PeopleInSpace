//
//  AsyncFunction.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 13/06/2021.
//

import KMPNativeCoroutinesCore

/// Wraps the `NativeSuspend` in an async function.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The result from the `nativeSuspend`.
/// - Throws: Errors thrown by the `nativeSuspend`.
public func asyncFunction<Result, Failure: Error, Unit>(for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>) async throws -> Result {
    let asyncFunctionActor = AsyncFunctionActor<Result, Unit>()
    return try await withTaskCancellationHandler {
        Task { await asyncFunctionActor.cancel() }
    } operation: {
        try await withUnsafeThrowingContinuation { continuation in
            Task {
                await asyncFunctionActor.setContinuation(continuation)
                let nativeCancellable = nativeSuspend({ output, unit in
                    Task { await asyncFunctionActor.continueWith(result: output) }
                    return unit
                }, { error, unit in
                    Task { await asyncFunctionActor.continueWith(error: error) }
                    return unit
                })
                await asyncFunctionActor.setNativeCancellable(nativeCancellable)
            }
        }
    }
}

internal actor AsyncFunctionActor<Result, Unit> {
    
    private var isCancelled = false
    private var nativeCancellable: NativeCancellable<Unit>? = nil
    
    func setNativeCancellable(_ nativeCancellable: @escaping NativeCancellable<Unit>) {
        guard !isCancelled else {
            _ = nativeCancellable()
            return
        }
        self.nativeCancellable = nativeCancellable
    }
    
    func cancel() {
        isCancelled = true
        _ = nativeCancellable?()
        nativeCancellable = nil
    }
    
    private var continuation: UnsafeContinuation<Result, Error>? = nil
    
    func setContinuation(_ continuation: UnsafeContinuation<Result, Error>) {
        self.continuation = continuation
    }
    
    func continueWith(result: Result) {
        continuation?.resume(returning: result)
        continuation = nil
    }
    
    func continueWith(error: Error) {
        continuation?.resume(throwing: error)
        continuation = nil
    }
}
