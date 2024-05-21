import Foundation
import Combine
import common
import KMPNativeCoroutinesAsync


@MainActor
class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0, longitude: 0)
        
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
    
    func startObservingISSPosition() async {
        do {
            let stream = asyncSequence(for: PeopleInSpaceRepositoryNativeKt.pollISSPosition(repository))
            for try await data in stream {
                self.issPosition = data
            }
        } catch {
            print("Failed with error: \(error)")
        }
    }
}


