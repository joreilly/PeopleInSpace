import SwiftUI
import common
import MapKit
import KMPNativeCoroutinesAsync

struct NativeISSMapView : View {
    @State var viewModel: ISSPositionViewModel
    @State var issPosition = IssPosition(latitude: 0, longitude: 0)
    
    var body: some View {
        
        VStack {
            let issCoordinatePosition = CLLocationCoordinate2D(latitude: issPosition.latitude, longitude: issPosition.longitude)
            let regionBinding = Binding<MKCoordinateRegion>(
                get: {
                    MKCoordinateRegion(center: issCoordinatePosition, span: MKCoordinateSpan(latitudeDelta: 150, longitudeDelta: 150))
                },
                set: { _ in }
            )

            MapReader { reader in
                Map(coordinateRegion: regionBinding, showsUserLocation: true,
                    annotationItems: [ Location(coordinate: issCoordinatePosition) ]) { (location) -> MapPin in
                    MapPin(coordinate: location.coordinate)
                }
            }
        }
        .task {
            do {
                let stream = asyncSequence(for: viewModel.position)
                for try await data in stream {
                    self.issPosition = data
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
}

struct Location: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}
