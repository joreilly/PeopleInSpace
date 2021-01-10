import SwiftUI
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
            }.onDisappear {
                self.peopleInSpaceViewModel.stopObservingPeopleUpdates()
            }
            
            MapView(issPosition: $peopleInSpaceViewModel.issPosition)
        }
    }
}

struct PersonView : View {
    var person: Assignment
    
    var body: some View {
        Text(person.name + " (" + person.craft + ")")
    }
}

