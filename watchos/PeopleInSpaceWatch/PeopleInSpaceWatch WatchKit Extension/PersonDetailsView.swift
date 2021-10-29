import SwiftUI
import common

struct PersonDetailsView: View {
    var viewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        ScrollView {
            VStack {
                ImageView(withURL: person.personImageUrl ?? "", width: 120, height: 120)

                Text(person.name).font(.headline)
                Spacer()

                Text(person.personBio ?? "").font(.body)
                    .fontWeight(.thin)
                    .multilineTextAlignment(.center)
                Spacer()
            }
            .padding()
        }
    }
}
