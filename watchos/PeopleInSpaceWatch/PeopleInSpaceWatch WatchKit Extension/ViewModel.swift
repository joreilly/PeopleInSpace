import Foundation
import common

class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    
    func fetch() {
        repository.startObservingPeopleUpdates(success: { data in
            self.people = data
        })        
    }
}

