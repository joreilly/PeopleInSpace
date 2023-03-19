//
//  NativeCancellable.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A function that cancels the coroutines job.
public typealias NativeCancellable<Unit> = () -> Unit
