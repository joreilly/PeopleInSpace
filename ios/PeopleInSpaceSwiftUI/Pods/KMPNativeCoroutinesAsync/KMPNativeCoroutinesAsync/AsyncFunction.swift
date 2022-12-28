//
//  AsyncFunction.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 13/06/2021.
//

import Dispatch
import KMPNativeCoroutinesCore

/// Wraps the `NativeSuspend` in an async function.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The result from the `nativeSuspend`.
/// - Throws: Errors thrown by the `nativeSuspend`.
public func asyncFunction<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>
) async throws -> Result {
    try await AsyncFunctionTask(nativeSuspend: nativeSuspend).awaitResult()
}

private class AsyncFunctionTask<Result, Failure: Error, Unit>: @unchecked Sendable {
    
    private let semaphore = DispatchSemaphore(value: 1)
    private var nativeCancellable: NativeCancellable<Unit>?
    private var result: Result? = nil
    private var error: Failure? = nil
    private var cancellationError: Failure? = nil
    private var continuation: UnsafeContinuation<Result, Error>? = nil
    
    init(nativeSuspend: NativeSuspend<Result, Failure, Unit>) {
        nativeCancellable = nativeSuspend({ result, unit in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.result = result
            if let continuation = self.continuation {
                continuation.resume(returning: result)
                self.continuation = nil
            }
            self.nativeCancellable = nil
            return unit
        }, { error, unit in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.error = error
            if let continuation = self.continuation {
                continuation.resume(throwing: error)
                self.continuation = nil
            }
            self.nativeCancellable = nil
            return unit
        }, { cancellationError, unit in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.cancellationError = cancellationError
            if let continuation = self.continuation {
                continuation.resume(throwing: CancellationError())
                self.continuation = nil
            }
            self.nativeCancellable = nil
            return unit
        })
    }
    
    func awaitResult() async throws -> Result {
        try await withTaskCancellationHandler {
            _ = nativeCancellable?()
            nativeCancellable = nil
        } operation: {
            try await withUnsafeThrowingContinuation { continuation in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                if let result = self.result {
                    continuation.resume(returning: result)
                } else if let error = self.error {
                    continuation.resume(throwing: error)
                } else if self.cancellationError != nil {
                    continuation.resume(throwing: CancellationError())
                } else {
                    guard self.continuation == nil else {
                        fatalError("Concurrent calls to awaitResult aren't supported")
                    }
                    self.continuation = continuation
                }
            }
        }
    }
}
