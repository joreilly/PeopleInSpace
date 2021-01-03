import SwiftUI
import common


struct ContentView: View {
    @StateObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    
    var body: some View {
        NavigationView {
            VStack {
                let issPosition = String(format: "ISS Position = (%f, %f)", peopleInSpaceViewModel.issPosition.latitude, peopleInSpaceViewModel.issPosition.longitude )
                HStack {
                    Text(issPosition)
                }
                .padding(EdgeInsets(top: 18, leading: 16, bottom: 0, trailing: 16))
                                   
                
                Button(action: {
                    peopleInSpaceViewModel.cancel()
                }) {
                    Text("Cancel")
                }
                List(peopleInSpaceViewModel.people, id: \.name) { person in
                    NavigationLink(destination: PersonDetailsView(peopleInSpaceViewModel: peopleInSpaceViewModel, person: person)) {
                        PersonView(peopleInSpaceViewModel: self.peopleInSpaceViewModel, person: person)
                    }
                }
                .navigationBarTitle(Text("People In Space"))
                .onAppear {
                    self.peopleInSpaceViewModel.startObservingPeopleUpdates()
                }.onDisappear {
                    self.peopleInSpaceViewModel.stopObservingPeopleUpdates()
                }
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
