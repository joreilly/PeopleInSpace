import SwiftUI
import MapKit
import common

struct ContentView: View {
    @ObservedObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    let timer = Timer.publish(every: 10, on: .main, in: .common).autoconnect()

    var body: some View {

        NavigationView {
            List(peopleInSpaceViewModel.people, id: \.name) { person in
                PersonView(person: person)
            }
            .onReceive(timer) { _ in
                self.peopleInSpaceViewModel.fetchISSPosition()
            }
            .onAppear {
                self.peopleInSpaceViewModel.startObserving()
                self.peopleInSpaceViewModel.fetchISSPosition()
            }.onDisappear {
                self.peopleInSpaceViewModel.stopObserving()
            }
            
            MapView(coordinate: CLLocationCoordinate2DMake(peopleInSpaceViewModel.issPosition.latitude, peopleInSpaceViewModel.issPosition.longitude))
        }
    }
}

struct PersonView : View {
    var person: Assignment
    
    var body: some View {
        Text(person.name + " (" + person.craft + ")")
    }
}

