//
//  ContentView.swift
//  PeopleInSpaceWatch WatchKit Extension
//

import SwiftUI
import common

struct ContentView: View {
    
    @ObservedObject var peopleInSpaceViewModel = PeopleInSpaceViewModel(repository: PeopleInSpaceRepository())
    
    var body: some View {
        VStack {
            List(peopleInSpaceViewModel.people, id: \.name) { person in
                PersonView(person: person)
            }
            .onAppear(perform: {
                self.peopleInSpaceViewModel.fetch()
            })
        }
    }
}

struct PersonView : View {
    var person: Assignment
    
    var body: some View {
        NavigationLink(person.name, destination: Text(person.craft).font(.subheadline))
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
