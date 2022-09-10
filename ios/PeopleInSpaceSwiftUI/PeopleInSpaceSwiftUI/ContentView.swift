import SwiftUI
import Combine
import common


struct ContentView: View {
    @StateObject var viewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())

    init() {
        UITableView.appearance().backgroundColor = .clear
        UITableViewCell.appearance().backgroundColor = .clear
    }


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
    
    let gradient = Gradient(colors: [Color(0xFFEBFB), Color(0xFFEDE6)])

    
    var body: some View {

        NavigationView {
            List(viewModel.people, id: \.name) { person in
                NavigationLink(destination: PersonDetailsView(viewModel: viewModel, person: person)) {
                    PersonView(viewModel: viewModel, person: person)
                }
                .listRowBackground(Color.clear)
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text("PeopleInSpace").font(.largeTitle.bold())
                }
            }
            .onAppear {
                viewModel.startObservingPeopleUpdates()
            }.onDisappear {
                viewModel.stopObservingPeopleUpdates()
            }
            .scrollContentBackground(.hidden)
            .background {
                LinearGradient(gradient: gradient, startPoint: .top, endPoint: .bottom)
                    .edgesIgnoringSafeArea(.vertical)
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



extension Color {
  init(_ hex: UInt, alpha: Double = 1) {
    self.init(
      .sRGB,
      red: Double((hex >> 16) & 0xFF) / 255,
      green: Double((hex >> 8) & 0xFF) / 255,
      blue: Double(hex & 0xFF) / 255,
      opacity: alpha
    )
  }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}


