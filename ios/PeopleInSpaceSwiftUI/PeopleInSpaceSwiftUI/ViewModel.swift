import Foundation
import Combine
import common


class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0, longitude: 0)
    
    private var subscription: AnyCancellable?
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
        
        subscription = IssPositionPublisher(repository: repository)
            //.map { position in String(format: "ISS Position = (%f, %f)", position.latitude, position.longitude ) }
            .assign(to: \.issPosition, on: self)
    }
    
    func startObservingPeopleUpdates() {
        repository.startObservingPeopleUpdates(success: { data in
            self.people = data
        })
    }
    
    func stopObservingPeopleUpdates() {
        repository.stopObservingPeopleUpdates()
    }
    
}


