//
//  ContentView.swift
//  PeopleInSpaceWatch WatchKit Extension
//

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
            .onAppear(perform: {
                self.viewModel.fetch()
            })
        }
    }
}

struct PersonView: View {
    var person: Assignment
    
    var body: some View {
        HStack {
            ImageView(withURL: person.personImageUrl ?? "", width: 50, height: 50)
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
