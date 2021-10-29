import SwiftUI
import common

struct PersonDetailsView: View {
    var viewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        ScrollView {
            VStack {
                Text(person.name).font(.subheadline)
                
                ImageView(withURL: person.personImageUrl ?? "", width: 120, height: 120)

                Text(person.personBio ?? "").font(.body).multilineTextAlignment(.center)
                Spacer()
            }
            .padding()
        }
    }
}
