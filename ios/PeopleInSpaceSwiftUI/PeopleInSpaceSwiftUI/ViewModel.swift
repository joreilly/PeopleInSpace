import Foundation
import common


class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    
    func fetch() {
        repository.fetchPeople(success: { data in
            self.people = data
        })        
    }
    
    func getPersonBio(personName: String) -> String {
        return repository.getPersonBio(personName: personName)
    }
    
    func getPersonImage(personName: String) -> String {
        return repository.getPersonImage(personName: personName)
    }

}

