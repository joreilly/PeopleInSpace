import SwiftUI
import common


struct ContentView: View {
    @ObservedObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    
    var body: some View {
        NavigationView {
            List(peopleInSpaceViewModel.people, id: \.name) { person in
                
                NavigationLink(destination: PersonDetailsView(peopleInSpaceViewModel: self.peopleInSpaceViewModel, person: person)) {
                    PersonView(person: person)
                }
                
            }
            .navigationBarTitle(Text("PeopleInSpace"), displayMode: .large)
            .onAppear(perform: {
                self.peopleInSpaceViewModel.fetch()
            })
        }
    }
}

struct PersonView: View {
    var person: Assignment
    
    var body: some View {
        HStack {
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
        VStack(alignment: .center, spacing: 10) {
            Text(person.name).font(.headline)
                
            Text(peopleInSpaceViewModel.getPersonBio(personName: person.name)).font(.body)
        }
        .padding(.leading)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
