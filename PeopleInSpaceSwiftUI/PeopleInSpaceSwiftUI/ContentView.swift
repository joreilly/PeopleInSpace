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
            ZStack {
                // Background color to match details screen
                Color(.systemGroupedBackground)
                    .edgesIgnoringSafeArea(.all)
                
                Observing(viewModel.uiState) { playerListUIState in
                    switch onEnum(of: playerListUIState) {
                    case .loading:
                        VStack(spacing: 16) {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle())
                            Text("Loading astronauts...")
                                .font(.headline)
                                .foregroundColor(.secondary)
                        }
                    case .error(let error):
                        VStack(spacing: 16) {
                            Image(systemName: "exclamationmark.triangle")
                                .font(.largeTitle)
                                .foregroundColor(.orange)
                            Text("Error: \(error)")
                                .font(.headline)
                                .foregroundColor(.primary)
                                .multilineTextAlignment(.center)
                            
                            Button(action: {
                                // Refresh action
                                viewModel = PersonListViewModel()
                            }) {
                                Label("Try Again", systemImage: "arrow.clockwise")
                                    .padding()
                                    .background(Color.blue)
                                    .foregroundColor(.white)
                                    .cornerRadius(8)
                            }
                        }
                        .padding()
                    case .success(let success):
                        ScrollView {
                            LazyVStack(spacing: 12) {
                                ForEach(success.result, id: \.name) { person in
                                    NavigationLink(value: person) {
                                        PersonView(person: person)
                                            .padding(.horizontal)
                                            .background(
                                                RoundedRectangle(cornerRadius: 12)
                                                    .fill(Color(.systemBackground))
                                                    .shadow(color: Color.black.opacity(0.05), radius: 5, x: 0, y: 2)
                                            )
                                            .padding(.horizontal)
                                    }
                                    .buttonStyle(PlainButtonStyle())
                                }
                            }
                            .padding(.vertical)
                        }
                    }
                }
            }
            .navigationDestination(for: Assignment.self) { person in
                PersonDetailsScreen(person: person)
            }
            .navigationBarTitle(Text("People In Space"))
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        // Refresh action
                        viewModel = PersonListViewModel()
                    }) {
                        Image(systemName: "arrow.clockwise")
                    }
                }
            }
        }
    }
}

struct PersonView: View {
    let person: Assignment

    var body: some View {
        HStack(spacing: 16) {
            // Person image with improved styling
            AsyncImage(url: URL(string: person.personImageUrl ?? "")) { image in
                image.resizable()
                    .aspectRatio(contentMode: .fill)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.gray.opacity(0.2), lineWidth: 1))
            } placeholder: {
                Circle()
                    .fill(Color.gray.opacity(0.2))
                    .overlay(
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .gray))
                    )
            }
            .frame(width: 70, height: 70)
            .shadow(color: .gray.opacity(0.3), radius: 3, x: 0, y: 1)
            
            // Person information with improved typography and layout
            VStack(alignment: .leading, spacing: 6) {
                Text(person.name)
                    .font(.headline)
                    .foregroundColor(.primary)
                
                Text(person.craft)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                if !person.nationality.isEmpty {
                    Text(person.nationality)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                // Add a subtle bio preview if available
                if let bio = person.personBio, !bio.isEmpty {
                    Text(bio.prefix(50) + (bio.count > 50 ? "..." : ""))
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .lineLimit(1)
                }
            }
            
            Spacer()
            
            // Add a chevron to indicate navigation
            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(.gray)
        }
        .padding(.vertical, 8)
        .contentShape(Rectangle())
    }
}


struct PersonDetailsScreen: View {
    let person: Assignment

    var body: some View {
        // Compose share text once for use in toolbar
        let shareText = "Astronaut: \(person.name) — Craft: \(person.craft)\(person.nationality.isEmpty ? "" : " — Nationality: \(person.nationality)")"
        ScrollView {
            VStack(alignment: .center, spacing: 0) {
                // Header with astronaut name and craft
                VStack(spacing: 8) {
                    Text(person.name)
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                        .multilineTextAlignment(.center)
                    
                    Text("Currently on \(person.craft)")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    .padding(.bottom, 16)
                }
                .padding(.top, 24)
                .padding(.horizontal)
                
                // Astronaut image with enhanced styling
                ZStack {
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color.gray.opacity(0.1))
                        .frame(height: 300)
                    
                    AsyncImage(url: URL(string: person.personImageUrl ?? "")) { image in
                        image.resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(height: 300)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                    } placeholder: {
                        if (person.personImageUrl ?? "").isEmpty {
                            VStack(spacing: 8) {
                                Image(systemName: "person.crop.circle.fill")
                                    .font(.system(size: 64))
                                    .foregroundColor(.secondary)
                                Text("No image available")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        } else {
                            VStack {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle())
                                Text("Loading image...")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .padding(.top, 8)
                            }
                        }
                    }
                    .frame(height: 300)
                }
                .padding(.horizontal)
                .padding(.bottom, 24)
                
                // Nationality section (shown if available)
                if !person.nationality.isEmpty {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Nationality")
                            .font(.title3)
                            .fontWeight(.semibold)
                            .foregroundColor(.primary)
                        Text(person.nationality)
                            .font(.body)
                            .foregroundColor(.primary)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .padding(24)
                    .background(
                        RoundedRectangle(cornerRadius: 16)
                            .fill(Color(.systemBackground))
                            .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 5)
                    )
                    .padding(.horizontal)
                    .padding(.bottom, 24)
                }
                
                // Bio section with card styling
                if let bio = person.personBio, !bio.isEmpty {
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Biography")
                            .font(.title2)
                            .fontWeight(.bold)
                            .foregroundColor(.primary)
                        
                        Text(bio)
                            .font(.body)
                            .foregroundColor(.primary)
                            .fixedSize(horizontal: false, vertical: true)
                            .lineSpacing(6)
                    }
                    .padding(24)
                    .background(
                        RoundedRectangle(cornerRadius: 16)
                            .fill(Color(.systemBackground))
                            .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 5)
                    )
                    .padding(.horizontal)
                } else {
                    VStack(alignment: .center, spacing: 8) {
                        Image(systemName: "info.circle")
                            .font(.largeTitle)
                            .foregroundColor(.secondary)
                        Text("No biography available")
                            .font(.headline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(24)
                    .background(
                        RoundedRectangle(cornerRadius: 16)
                            .fill(Color.gray.opacity(0.1))
                    )
                    .padding(.horizontal)
                }
                
                // Additional information section
                VStack(alignment: .leading, spacing: 16) {
                    Text("Additional Information")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    
                    HStack {
                        VStack(alignment: .leading, spacing: 8) {
                            Label("Spacecraft", systemImage: "airplane.circle.fill")
                                .font(.headline)
                            Text(person.craft)
                                .font(.body)
                                .foregroundColor(.secondary)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        
                        Divider()
                            .frame(height: 40)
                        
                        VStack(alignment: .leading, spacing: 8) {
                            Label("Mission", systemImage: "star.circle.fill")
                                .font(.headline)
                            Text("Active")
                                .font(.body)
                                .foregroundColor(.green)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }
                .padding(24)
                .background(
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color(.systemBackground))
                        .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 5)
                )
                .padding(.horizontal)
                .padding(.top, 16)
                .padding(.bottom, 32)
            }
        }
        .background(Color(.systemGroupedBackground).edgesIgnoringSafeArea(.all))
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                ShareLink(item: shareText) {
                    Image(systemName: "square.and.arrow.up")
                }
            }
        }
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
