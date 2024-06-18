import Foundation
import SwiftUI
import MapKit
import common


struct ISSPositionScreen: View {
    @State var viewModel = ISSPositionViewModel()
        
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
    let viewModel: ISSPositionViewModel
    
    func makeUIViewController(context: Context) -> UIViewController {
        SharedViewControllers().ISSPositionContentViewController(
            viewModel: viewModel,
            nativeViewFactory: iOSNativeViewFactory.shared
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

