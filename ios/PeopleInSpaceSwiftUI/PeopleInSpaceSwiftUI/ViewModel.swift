import Foundation
import Combine
import common


class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0.0, longitude: 0.0)
    
    private var subscription: AnyCancellable?
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
        
        subscription = IssPositionPublisher(repository: repository)
            .assign(to: \.issPosition, on: self)
    }
    
    func cancel() {
        subscription?.cancel()
    }
    
    func startObservingPeopleUpdates() {
        repository.startObservingPeopleUpdates(success: { data in
            self.people = data
        })
    }
    
    func stopObservingPeopleUpdates() {
        repository.stopObservingPeopleUpdates()
    }
    
    func getPersonBio(personName: String) -> String {
        return repository.getPersonBio(personName: personName)
    }
    
    func getPersonImage(personName: String) -> String {
        return repository.getPersonImage(personName: personName)
    }


    
}


