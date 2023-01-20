//
//  AsyncResult.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 28/06/2021.
//

import KMPNativeCoroutinesCore

/// Awaits the `NativeSuspend` and returns the result.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The `Result` from the `nativeSuspend`.
public func asyncResult<Output, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Output, Failure, Unit>
) async -> Result<Output, Error> {
    do {
        return .success(try await asyncFunction(for: nativeSuspend))
    } catch {
        return .failure(error)
    }
}
