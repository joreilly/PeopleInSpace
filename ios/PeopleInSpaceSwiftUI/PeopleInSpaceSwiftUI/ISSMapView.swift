import Foundation
import SwiftUI
import MapKit
import common


struct ISSMapView: View {
    @Binding var issPosition: IssPosition
    
    var body: some View {
        let issCoordinatePosition = CLLocationCoordinate2D(latitude: issPosition.latitude, longitude: issPosition.longitude)
        let regionBinding = Binding<MKCoordinateRegion>(
            get: {
                MKCoordinateRegion(center: issCoordinatePosition, span: MKCoordinateSpan(latitudeDelta: 150, longitudeDelta: 150))
            },
            set: { _ in }
        )
        VStack {
            Text("\(issPosition.latitude), \(issPosition.longitude)")
            Map(coordinateRegion: regionBinding, showsUserLocation: true,
                annotationItems: [ Location(coordinate: issCoordinatePosition) ]) { (location) -> MapPin in
                MapPin(coordinate: location.coordinate)
            }
        }
    }
}


struct Location: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}

struct MapView: View {
    let issPosition: IssPosition
    let region: Binding<MKCoordinateRegion>
    
    var body: some View {
        Map(coordinateRegion: region, showsUserLocation: true,
            annotationItems: [ Location(coordinate: .init(latitude: issPosition.latitude, longitude: issPosition.longitude)) ]) { (location) -> MapPin in
            MapPin(coordinate: location.coordinate)
        }
    }
}
