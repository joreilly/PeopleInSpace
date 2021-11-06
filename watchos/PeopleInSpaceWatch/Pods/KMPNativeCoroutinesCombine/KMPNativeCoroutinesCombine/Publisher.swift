//
//  Publisher.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 06/06/2021.
//

import Combine
import KMPNativeCoroutinesCore

/// Creates an `AnyPublisher` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> AnyPublisher<Output, Failure> {
    return NativeFlowPublisher(nativeFlow: nativeFlow)
        .eraseToAnyPublisher()
}

internal struct NativeFlowPublisher<Output, Failure: Error, Unit>: Publisher {
    
    typealias Output = Output
    typealias Failure = Failure
    
    let nativeFlow: NativeFlow<Output, Failure, Unit>
    
    func receive<S>(subscriber: S) where S : Subscriber, Failure == S.Failure, Output == S.Input {
        let subscription = NativeFlowSubscription(nativeFlow: nativeFlow, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
}

internal class NativeFlowSubscription<Output, Failure, Unit, S: Subscriber>: Subscription where S.Input == Output, S.Failure == Failure {
    
    private var nativeCancellable: NativeCancellable<Unit>? = nil
    private var subscriber: S?
    
    init(nativeFlow: NativeFlow<Output, Failure, Unit>, subscriber: S) {
        self.subscriber = subscriber
        nativeCancellable = nativeFlow({ item, unit in
            _ = self.subscriber?.receive(item)
            return unit
        }, { error, unit in
            if let error = error {
                self.subscriber?.receive(completion: .failure(error))
            } else {
                self.subscriber?.receive(completion: .finished)
            }
            return unit
        })
    }
    
    func request(_ demand: Subscribers.Demand) { }
    
    func cancel() {
        subscriber = nil
        _ = nativeCancellable?()
        nativeCancellable = nil
    }
}
