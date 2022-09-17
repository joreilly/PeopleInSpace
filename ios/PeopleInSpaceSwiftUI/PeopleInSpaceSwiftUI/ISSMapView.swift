import Foundation
import SwiftUI
import MapKit
import common


struct ISSMapView: View {
    @ObservedObject var viewModel: PeopleInSpaceViewModel
    
    
    var body: some View {
        let issCoordinatePosition = CLLocationCoordinate2D(latitude: viewModel.issPosition.latitude, longitude: viewModel.issPosition.longitude)
        let regionBinding = Binding<MKCoordinateRegion>(
            get: {
                MKCoordinateRegion(center: issCoordinatePosition, span: MKCoordinateSpan(latitudeDelta: 150, longitudeDelta: 150))
            },
            set: { _ in }
        )
        VStack {
            Text("\(viewModel.issPosition.latitude), \(viewModel.issPosition.longitude)")
            Map(coordinateRegion: regionBinding, showsUserLocation: true,
                annotationItems: [ Location(coordinate: issCoordinatePosition) ]) { (location) -> MapPin in
                MapPin(coordinate: location.coordinate)
            }
        }
        .task {
            await viewModel.startObservingISSPosition()
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
