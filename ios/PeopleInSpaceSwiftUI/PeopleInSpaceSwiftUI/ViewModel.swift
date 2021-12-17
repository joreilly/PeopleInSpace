import Foundation
import Combine
import common
import KMPNativeCoroutinesAsync


@MainActor
class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0, longitude: 0)
    
    private var fetchPeopleTask: Task<(), Never>? = nil
    private var pollISSPositionTask: Task<(), Never>? = nil
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
        
        pollISSPositionTask = Task {
            do {
                let stream = asyncStream(for: repository.pollISSPositionNative())
                for try await data in stream {
                    self.issPosition = data
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    func startObservingPeopleUpdates() {
        fetchPeopleTask = Task {
            do {
                let stream = asyncStream(for: repository.fetchPeopleAsFlowNative())
                for try await data in stream {
                    self.people = data
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    func stopObservingPeopleUpdates() {
        fetchPeopleTask?.cancel()
    }
    
}


