import Combine
import common


public struct IssPositionPublisher: Publisher {
    public typealias Output = IssPosition
    public typealias Failure = Never
    
    private let repository: PeopleInSpaceRepository
    public init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }

    public func receive<S: Subscriber>(subscriber: S) where S.Input == IssPosition, S.Failure == Failure {
        let subscription = IssPositionSubscription(repository: repository, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
    
    final class IssPositionSubscription<S: Subscriber>: Subscription where S.Input == IssPosition, S.Failure == Failure {
        private var subscriber: S?
        private var job: Kotlinx_coroutines_coreJob? = nil

        private let repository: PeopleInSpaceRepository

        init(repository: PeopleInSpaceRepository, subscriber: S) {
            self.repository = repository
            self.subscriber = subscriber
          
            job = repository.iosPollISSPosition().subscribe(
                scope: repository.iosScope,
                onEach: { position in
                    subscriber.receive(position!)
                },
                onComplete: { subscriber.receive(completion: .finished) },
                onThrow: { error in debugPrint(error) }
            )
        }
      
        func cancel() {
            subscriber = nil
            job?.cancel(cause: nil)
        }

        func request(_ demand: Subscribers.Demand) {}
    }
}
