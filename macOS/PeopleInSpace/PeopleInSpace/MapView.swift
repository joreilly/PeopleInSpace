import SwiftUI
import MapKit

struct MapView {
    var coordinate: CLLocationCoordinate2D

    func makeMapView() -> MKMapView {
        MKMapView(frame: .zero)
    }
    
    func updateMapView(_ view: MKMapView, context: Context) {
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

#if os(macOS)

extension MapView: NSViewRepresentable {
    func makeNSView(context: Context) -> MKMapView {
        makeMapView()
    }
    
    func updateNSView(_ nsView: MKMapView, context: Context) {
        updateMapView(nsView, context: context)
    }
}

#else

extension MapView: UIViewRepresentable {
    func makeUIView(context: Context) -> MKMapView {
        makeMapView()
    }
    
    func updateUIView(_ uiView: MKMapView, context: Context) {
        updateMapView(uiView, context: context)
    }
}

#endif

