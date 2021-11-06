import Foundation
import common
import Combine
import KMPNativeCoroutinesCombine

class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    
    private var peopleCancellable: AnyCancellable?
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    
    func fetch() {
        peopleCancellable = createPublisher(for: repository.fetchPeopleAsFlowNative())
            .receive(on: DispatchQueue.main)
            .sink(receiveCompletion: { completion in
                debugPrint(completion)
            }, receiveValue: { [weak self] value in
                self?.people = value
            })
    }
}

