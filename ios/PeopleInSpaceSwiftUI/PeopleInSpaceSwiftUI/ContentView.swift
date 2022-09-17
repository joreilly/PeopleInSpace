import SwiftUI
import Combine
import common


struct ContentView: View {
    @StateObject var viewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())

    var body: some View {
        TabView {
            PeopleListView(viewModel: viewModel)
                .tabItem {
                    Label("People", systemImage: "person")
                }
            ISSMapView(viewModel: viewModel)
                .tabItem {
                    Label("Map", systemImage: "location")
                }
        }
    }
}

struct PeopleListView: View {
    @ObservedObject var viewModel: PeopleInSpaceViewModel
    
    var body: some View {
        NavigationView {
            List(viewModel.people, id: \.name) { person in
                NavigationLink(destination: PersonDetailsView(viewModel: viewModel, person: person)) {
                    PersonView(viewModel: viewModel, person: person)
                }
            }
            .navigationBarTitle(Text("People In Space"))
            .navigationBarTitleDisplayMode(.inline)
            .task {
                await viewModel.startObservingPeopleUpdates()
            }
        }
    }
}

struct PersonView: View {
    var viewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        HStack {
            AsyncImage(url: URL(string: person.personImageUrl ?? "")) { image in
                 image.resizable()
                    .aspectRatio(contentMode: .fit)
            } placeholder: {
                ProgressView()
            }
            .frame(width: 64, height: 64)

            
            VStack(alignment: .leading) {
                Text(person.name).font(.headline)
                Text(person.craft).font(.subheadline)
            }
        }
    }
}


struct PersonDetailsView: View {
    var viewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        ScrollView {
            VStack(alignment: .center, spacing: 32) {
                Text(person.name).font(.title)
                
                AsyncImage(url: URL(string: person.personImageUrl ?? "")) { image in
                     image.resizable()
                        .aspectRatio(contentMode: .fit)
                } placeholder: {
                    ProgressView()
                }
                .frame(width: 240, height: 240)

                Text(person.personBio ?? "").font(.body)
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
