import SwiftUI
import common
import MapKit

struct NativeISSMapView : View {
    var viewModel: ISSPositionViewModel
    
    var body: some View {
        VStack {
            Observing(viewModel.position) { issPosition in
                let issCoordinatePosition = CLLocationCoordinate2D(latitude: issPosition.latitude, longitude: issPosition.longitude)
                let regionBinding = Binding<MKCoordinateRegion>(
                    get: {
                        MKCoordinateRegion(center: issCoordinatePosition, span: MKCoordinateSpan(latitudeDelta: 150, longitudeDelta: 150))
                    },
                    set: { _ in }
                )

                Map(coordinateRegion: regionBinding, showsUserLocation: true,
                    annotationItems: [ Location(coordinate: issCoordinatePosition) ]) { (location) -> MapPin in
                    MapPin(coordinate: location.coordinate)
                }
            }
        }
    }
}

struct Location: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}
