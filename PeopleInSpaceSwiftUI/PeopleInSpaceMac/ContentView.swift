import SwiftUI
import common


struct ContentView: View {
    @ObservedObject var viewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    
    var body: some View {
        NavigationView {
            List(viewModel.people, id: \.name) { person in
                PersonView(person: person)
            }
            .task {
                await viewModel.startObservingPeopleUpdates()
            }

            ISSMapView(viewModel: viewModel)
            //MapView(issPosition: $viewModel.issPosition)
        }.toolbar {
            ToolbarItem(placement: .navigation) {
                Button(action: toggleSidebar, label: { // 1
                    Image(systemName: "sidebar.leading")
                })
            }
        }
    }
    private func toggleSidebar() {
        #if os(iOS)
        #else
        NSApp.keyWindow?.firstResponder?.tryToPerform(#selector(NSSplitViewController.toggleSidebar(_:)), with: nil)
        #endif
    }
}

struct PersonView : View {
    var person: Assignment
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(person.name).font(.headline)
            Text(person.craft).font(.subheadline)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
