import Foundation
import SwiftUI
import UIKit
import Common

class iOSNativeViewFactory : ui.NativeViewFactory {
    static var shared = iOSNativeViewFactory()

    func createISSMapView(viewModel: viewmodel.ISSPositionViewModel) -> UIViewController {
        let mapView = NativeISSMapView(viewModel: viewModel)
        return UIHostingController(rootView: mapView)
    }
}
