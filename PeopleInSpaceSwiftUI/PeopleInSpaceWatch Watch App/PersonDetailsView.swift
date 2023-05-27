import SwiftUI
import common

struct PersonDetailsView: View {
    var viewModel: PeopleInSpaceViewModel
    var person: Assignment
    
    var body: some View {
        ScrollView {
            VStack {
                AsyncImage(url: URL(string: person.personImageUrl ?? "")) { image in
                     image.resizable()
                        .aspectRatio(contentMode: .fit)
                } placeholder: {
                    ProgressView()
                }
                .frame(width: 120, height: 120)

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
