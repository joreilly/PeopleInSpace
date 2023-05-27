import SwiftUI
import common

struct ContentView: View {
    
    @ObservedObject var viewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    
    var body: some View {
        VStack {
            List(viewModel.people, id: \.name) { person in
                NavigationLink(destination: PersonDetailsView(viewModel: viewModel, person: person)) {
                    PersonView(person: person)
                }
            }
            .task {
                await viewModel.startObservingPeopleUpdates()
            }
        }
    }
}

struct PersonView: View {
    var person: Assignment
    
    var body: some View {
        HStack {
            AsyncImage(url: URL(string: person.personImageUrl ?? "")) { image in
                 image.resizable()
                    .aspectRatio(contentMode: .fit)
            } placeholder: {
                ProgressView()
            }
            .frame(width: 50, height: 50)

            VStack(alignment: .leading) {
                Text(person.name).font(.caption)
                Text(person.craft)
                    .font(.caption)
                    .fontWeight(.thin)
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
