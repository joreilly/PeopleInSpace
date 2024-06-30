import Foundation
import Combine
import common
import KMPNativeCoroutinesAsync


@MainActor
class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
        
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    
    func startObservingPeopleUpdates() async {
        do {
            let stream = asyncSequence(for: PeopleInSpaceRepositoryNativeKt.fetchPeopleAsFlow(repository))
            for try await data in stream {
                self.people = data
            }
        } catch {
            print("Failed with error: \(error)")
        }
    }
    
}


