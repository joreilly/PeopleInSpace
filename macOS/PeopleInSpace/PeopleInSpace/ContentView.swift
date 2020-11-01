import SwiftUI
import MapKit
import common

struct ContentView: View {
    @ObservedObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())

    var body: some View {

        NavigationView {
            List(peopleInSpaceViewModel.people, id: \.name) { person in
                PersonView(person: person)
            }
            .onAppear {
                self.peopleInSpaceViewModel.startObservingPeopleUpdates()
                self.peopleInSpaceViewModel.startObservingISSPosition()
            }.onDisappear {
                self.peopleInSpaceViewModel.stopObservingPeopleUpdates()
                self.peopleInSpaceViewModel.stopObservingISSPosition()
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

