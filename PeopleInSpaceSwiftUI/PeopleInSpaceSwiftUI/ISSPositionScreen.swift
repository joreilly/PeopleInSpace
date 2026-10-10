import Foundation
import SwiftUI
import MapKit
import Common


struct ISSPositionScreen: View {
    @State var viewModel = di.issPositionViewModel()
        
    var body: some View {
        NavigationView {
            VStack {
                ISSPositionContentViewController(viewModel: viewModel)
            }
            .navigationBarTitle(Text("ISS Position"))
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct ISSPositionContentViewController: UIViewControllerRepresentable {
    let viewModel: viewmodel.ISSPositionViewModel
    
    func makeUIViewController(context: Context) -> UIViewController {
        ui.ISSPositionContentViewController(
            viewModel: viewModel,
            nativeViewFactory: iOSNativeViewFactory.shared
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

