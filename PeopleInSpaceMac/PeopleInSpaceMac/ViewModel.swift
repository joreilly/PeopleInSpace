import Foundation
import Combine
import common
import KMPNativeCoroutinesCombine


class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0.0, longitude: 0.0)
    
    private var positionCancellable: AnyCancellable?
    private var peopleCancellable: AnyCancellable?
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
        
        positionCancellable = createPublisher(for: repository.pollISSPosition())
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { completion in
                debugPrint(completion)
            }, receiveValue: { [weak self] value in
                self?.issPosition = value
            })
    }
    
    func startObservingPeopleUpdates() {
        peopleCancellable = createPublisher(for: repository.fetchPeopleAsFlow())
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { completion in
                debugPrint(completion)
            }, receiveValue: { [weak self] value in
                self?.people = value
            })
    }
    
    func stopObservingPeopleUpdates() {
        peopleCancellable?.cancel()
    }
    
}
