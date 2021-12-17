//
//  AsyncStream.swift
//  AsyncStream
//
//  Created by Rick Clephas on 15/07/2021.
//

import KMPNativeCoroutinesCore

/// Wraps the `NativeFlow` in an `AsyncThrowingStream`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An stream that yields the collected values.
public func asyncStream<Output, Failure: Error, Unit>(for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>) -> AsyncThrowingStream<Output, Error> {
    let asyncStreamActor = AsyncStreamActor<Output, Unit>()
    return AsyncThrowingStream { continuation in
        continuation.onTermination = { @Sendable _ in
            Task { await asyncStreamActor.cancel() }
        }
        Task {
            let nativeCancellable = nativeFlow({ item, unit in
                continuation.yield(item)
                return unit
            }, { error, unit in
                if let error = error {
                    continuation.finish(throwing: error)
                } else {
                    continuation.finish(throwing: nil)
                }
                return unit
            })
            await asyncStreamActor.setNativeCancellable(nativeCancellable)
        }
    }
}

internal actor AsyncStreamActor<Output, Unit> {
    
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
}
