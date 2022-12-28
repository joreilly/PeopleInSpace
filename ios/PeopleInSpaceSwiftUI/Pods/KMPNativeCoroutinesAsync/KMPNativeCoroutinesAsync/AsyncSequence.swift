//
//  AsyncSequence.swift
//  AsyncSequence
//
//  Created by Rick Clephas on 06/03/2022.
//

import Dispatch
import KMPNativeCoroutinesCore

/// Wraps the `NativeFlow` in a `NativeFlowAsyncSequence`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: A `NativeFlowAsyncSequence` that yields the collected values.
public func asyncSequence<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> NativeFlowAsyncSequence<Output, Error, Unit> {
    return NativeFlowAsyncSequence(nativeFlow: nativeFlow)
}

public struct NativeFlowAsyncSequence<Output, Failure: Error, Unit>: AsyncSequence {
    public typealias Element = Output
    
    var nativeFlow: NativeFlow<Output, Failure, Unit>
    
    public class Iterator: AsyncIteratorProtocol, @unchecked Sendable {
        
        private let semaphore = DispatchSemaphore(value: 1)
        private var nativeCancellable: NativeCancellable<Unit>?
        private var item: (Output, () -> Unit)? = nil
        private var result: Failure?? = Optional.none
        private var cancellationError: Failure? = nil
        private var continuation: UnsafeContinuation<Output?, Error>? = nil
        
        init(nativeFlow: NativeFlow<Output, Failure, Unit>) {
            nativeCancellable = nativeFlow({ item, next, unit in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                if let continuation = self.continuation {
                    continuation.resume(returning: item)
                    self.continuation = nil
                    return next()
                } else {
                    self.item = (item, next)
                    return unit
                }
            }, { error, unit in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                self.result = Optional.some(error)
                if let continuation = self.continuation {
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else {
                        continuation.resume(returning: nil)
                    }
                    self.continuation = nil
                }
                self.nativeCancellable = nil
                return unit
            }, { cancellationError, unit in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                self.cancellationError = cancellationError
                if let continuation = self.continuation {
                    continuation.resume(returning: nil)
                    self.continuation = nil
                }
                self.nativeCancellable = nil
                return unit
            })
        }
        
        public func next() async throws -> Output? {
            return try await withTaskCancellationHandler {
                _ = nativeCancellable?()
                nativeCancellable = nil
            } operation: {
                try await withUnsafeThrowingContinuation { continuation in
                    self.semaphore.wait()
                    defer { self.semaphore.signal() }
                    if let (item, next) = self.item {
                        continuation.resume(returning: item)
                        _ = next()
                        self.item = nil
                    } else if let result = self.result {
                        if let error = result {
                            continuation.resume(throwing: error)
                        } else {
                            continuation.resume(returning: nil)
                        }
                    } else if self.cancellationError != nil {
                        continuation.resume(throwing: CancellationError())
                    } else {
                        guard self.continuation == nil else {
                            fatalError("Concurrent calls to next aren't supported")
                        }
                        self.continuation = continuation
                    }
                }
            }
        }
    }
    
    public func makeAsyncIterator() -> Iterator {
        return Iterator(nativeFlow: nativeFlow)
    }
}

