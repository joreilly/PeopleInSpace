import Foundation
import common


class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0.0, longitude: 0.0)
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    
    func startObservingPeopleUpdates() {
        repository.startObservingPeopleUpdates(success: { data in
            self.people = data
        })
    }
    
    func stopObservingPeopleUpdates() {
        repository.stopObservingPeopleUpdates()
    }
    
    func startObservingISSPosition() {
        repository.startObservingISSPosition(success: { data in
            self.issPosition = data
        })
    }
    
    func stopObservingISSPosition() {
        repository.stopObservingISSPosition()
    }

    
    func getPersonBio(personName: String) -> String {
        return repository.getPersonBio(personName: personName)
    }
    
    func getPersonImage(personName: String) -> String {
        return repository.getPersonImage(personName: personName)
    }

}

