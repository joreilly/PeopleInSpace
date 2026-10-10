import Foundation
import SwiftUI
import UIKit
import Common

enum iOSNativeViewFactory {
    static func createISSMapView(viewModel: viewmodel.ISSPositionViewModel) -> UIViewController {
        let mapView = NativeISSMapView(viewModel: viewModel)
        return UIHostingController(rootView: mapView)
    }
}
