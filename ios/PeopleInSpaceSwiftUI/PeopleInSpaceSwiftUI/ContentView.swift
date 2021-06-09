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
            ISSMapView(issPosition: $viewModel.issPosition)
                .tabItem {
                    Label("Map", systemImage: "location")
                }
        }
    }
}

struct PeopleListView: View {
    @ObservedObject var viewModel: PeopleInSpaceViewModel
    
    
    @State private var query = ""
    private var filteredPeopleList: [Assignment] {
        query.isEmpty ? viewModel.people : viewModel.people.filter { $0.name.contains(query)}
    }
    
    var body: some View {
        NavigationView {
            List(viewModel.people, id: \.name) { person in
                NavigationLink(destination: PersonDetailsView(viewModel: viewModel, person: person)) {
                    PersonView(viewModel: viewModel, person: person)
                }
            }
            .navigationBarTitle(Text("People In Space"))
            .refreshable {
                await viewModel.getPeopleAsync()
            }
            .navigationBarTitle(Text("People In Space"))
            .onAppear {
                print("onAppear")
            }
            .onDisappear {
                print("onDisappear")
            }
            .searchable(text: $query)
        }.task {
            await viewModel.getPeopleAsync()
        }
    }
}

struct PersonView: View {
    var viewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        HStack {
            let personImageUrl = URL(string: viewModel.getPersonImage(personName: person.name))

            let imageSize = 64.0
            AsyncImage(url: personImageUrl) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: imageSize, height: imageSize)
            } placeholder: {
                Image(systemName: "photo")
                    .frame(width: imageSize, height: imageSize)
            }

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
                
                let personImageUrl = URL(string: viewModel.getPersonImage(personName: person.name))

                AsyncImage(url: personImageUrl) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 240, height: 240)
                } placeholder: {
                    Image(systemName: "photo")
                        .frame(width: 240, height: 240)
                }
                
                Text(viewModel.getPersonBio(personName: person.name)).font(.body)
                Spacer()
            }
            .padding()
        }.task {
            await viewModel.getPeopleAsync()
        }

        
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}


