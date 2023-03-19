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
