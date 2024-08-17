import SwiftUI
import common


struct ContentView: View {
    var body: some View {
        TabView {
            PeopleListScreen()
                .tabItem {
                    Label("People", systemImage: "person")
                }
            ISSPositionScreen()
                .tabItem {
                    Label("ISS Position", systemImage: "location")
                }
        }
    }
}

struct PeopleListScreen: View {
    @State var viewModel = PersonListViewModel()
    
    @State private var path: [Assignment] = []
    
    var body: some View {
        NavigationStack(path: $path) {
            VStack {
                Observing(viewModel.uiState) { playerListUIState in
                    switch onEnum(of: playerListUIState) {
                    case .loading:
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle())
                    case .error(let error):
                        Text("Error: \(error)")
                    case .success(let success):
                        List(success.result, id: \.name) { person in
                            NavigationLink(value: person) {
                                PersonView(person: person)
                            }
                        }
                    }
                }
            }
            .navigationDestination(for: Assignment.self) { person in
                PersonDetailsScreen(person: person)
            }
            .navigationBarTitle(Text("People In Space"))
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct PersonView: View {
    let person: Assignment

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


struct PersonDetailsScreen: View {
    let person: Assignment

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
