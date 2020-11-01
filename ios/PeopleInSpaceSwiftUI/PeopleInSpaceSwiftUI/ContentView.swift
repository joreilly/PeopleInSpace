import SwiftUI
import common


struct ContentView: View {
    @ObservedObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    
    var body: some View {
        NavigationView {
            List(peopleInSpaceViewModel.people, id: \.name) { person in
                NavigationLink(destination: PersonDetailsView(peopleInSpaceViewModel: self.peopleInSpaceViewModel, person: person)) {
                    PersonView(peopleInSpaceViewModel: self.peopleInSpaceViewModel, person: person)
                }
            }
            .navigationBarTitle(Text("PeopleInSpace"), displayMode: .large)
            .onAppear {
                self.peopleInSpaceViewModel.startObserving()
            }.onDisappear {
                self.peopleInSpaceViewModel.stopObserving()
            }
        }
    }
}

struct PersonView: View {
    var peopleInSpaceViewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        HStack {
            ImageView(withURL: peopleInSpaceViewModel.getPersonImage(personName: person.name), width: 64, height: 64)
            VStack(alignment: .leading) {
                Text(person.name).font(.headline)
                Text(person.craft).font(.subheadline)
            }
        }
    }
}


struct PersonDetailsView: View {
    var peopleInSpaceViewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        ScrollView {
            VStack(alignment: .center, spacing: 32) {
                Text(person.name).font(.title)
                
                ImageView(withURL: peopleInSpaceViewModel.getPersonImage(personName: person.name), width: 240, height: 240)
                
                Text(peopleInSpaceViewModel.getPersonBio(personName: person.name)).font(.body)
                Spacer()
            }
            .padding()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
