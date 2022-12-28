//
//  NativeCallback.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A callback with a single argument.
///
/// The return value is provided as the second argument.
/// This way Swift doesn't known what it is/how to get it.
public typealias NativeCallback<T, Unit> = (T, Unit) -> Unit

/// A callback with two arguments.
///
/// The return value is provided as the third argument.
/// This way Swift doesn't known what it is/how to get it.
public typealias NativeCallback2<T1, T2, Unit> = (T1, T2, Unit) -> Unit
