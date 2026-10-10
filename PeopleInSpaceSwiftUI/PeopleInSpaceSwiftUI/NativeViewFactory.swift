import Foundation
import SwiftUI
import UIKit
import Common
import KotlinRuntime

// Swift Export protocols for Kotlin interfaces require a KotlinBase subclass (Swift-side
// subclassing of KotlinBase works from Kotlin 2.5.0-Beta2).
class iOSNativeViewFactory : KotlinBase, ui.NativeViewFactory {
    static var shared = iOSNativeViewFactory()

    func createISSMapView(viewModel: viewmodel.ISSPositionViewModel) -> UIViewController {
        let mapView = NativeISSMapView(viewModel: viewModel)
        return UIHostingController(rootView: mapView)
    }
}
