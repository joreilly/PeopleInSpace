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
            .listStyle(SidebarListStyle())
            .onAppear(perform: {
                self.peopleInSpaceViewModel.fetchPeople()
                self.peopleInSpaceViewModel.fetchISSPosition()
            })
            
            MapView(coordinate: CLLocationCoordinate2DMake(peopleInSpaceViewModel.issPosition.latitude, peopleInSpaceViewModel.issPosition.longitude))
        }
    }
}


struct DetailView: View {
    let text: String

    var body: some View {
        Text(text)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct PersonView : View {
    var person: Assignment
    
    var body: some View {
        Text(person.name + " (" + person.craft + ")")
    }
}

