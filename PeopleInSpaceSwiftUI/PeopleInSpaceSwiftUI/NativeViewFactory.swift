import Foundation
import SwiftUI
import UIKit
import common

class iOSNativeViewFactory : NativeViewFactory {
    static var shared = iOSNativeViewFactory()

    func createISSMapView(viewModel: ISSPositionViewModel) -> UIViewController {
        let mapView = NativeISSMapView(viewModel: viewModel)
        return UIHostingController(rootView: mapView)
    }
}
