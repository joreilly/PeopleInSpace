import SwiftUI
import MapKit
import common

struct MapView: NSViewRepresentable {
    typealias NSViewType = MKMapView
    
    @Binding var issPosition: IssPosition
    

    func makeNSView(context: Context) -> MKMapView {
        MKMapView(frame: .zero)
    }
    
    func updateNSView(_ view: MKMapView, context: Context) {
        let coordinate = CLLocationCoordinate2DMake(issPosition.latitude, issPosition.longitude)

        let span = MKCoordinateSpan(latitudeDelta: 150, longitudeDelta: 150)
        let region = MKCoordinateRegion(center: coordinate, span: span)
        view.showsZoomControls = true
        view.setRegion(region, animated: false)

        view.removeAnnotations(view.annotations)
        let annotation = MKPointAnnotation()
        annotation.coordinate = coordinate
        view.addAnnotation(annotation)
    }
}



