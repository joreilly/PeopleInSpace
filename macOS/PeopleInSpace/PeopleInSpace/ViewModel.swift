import Foundation
import common


class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0.0, longitude: 0.0)
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    
    func fetchPeople() {
        repository.fetchPeople(success: { data in
            self.people = data
        })        
    }
    
    func fetchISSPosition() {
        repository.fetchISSPosition { (data, error) in
            if let issPosition = data {
                self.issPosition = issPosition
            }
        }
    }

}

