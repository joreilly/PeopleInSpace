import Foundation
import Combine
import common
import KMPNativeCoroutinesCombine

class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0, longitude: 0)
    
    private var cancellable: AnyCancellable?
    //private var subscription: AnyCancellable?
    
    private let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
        
        let publisher = createPublisher(for: repository.pollISSPositionNative())
        
        cancellable?.cancel()
        cancellable = publisher.sink { completion in
            print("Received completion: \(completion)")
        } receiveValue: { value in
            //print("Received value: \(value)")
            self.issPosition = value
        }

//        subscription = IssPositionPublisher(repository: repository)
//            //.map { position in String(format: "ISS Position = (%f, %f)", position.latitude, position.longitude ) }
//            .assign(to: \.issPosition, on: self)
    }
    
    func startObservingPeopleUpdates() {
//        repository.startObservingPeopleUpdates(success: { data in
//            self.people = data
//        })
    }
    
    func stopObservingPeopleUpdates() {
        //repository.stopObservingPeopleUpdates()
    }
    
}


