import Foundation
import Combine
import common


class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPositionString = ""
    
    private var subscription: AnyCancellable?
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
        
        subscription = IssPositionPublisher(repository: repository)
            .map { position in String(format: "ISS Position = (%f, %f)", position.latitude, position.longitude ) }
            .assign(to: \.issPositionString, on: self)
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


