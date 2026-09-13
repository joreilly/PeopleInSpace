package dev.johnoreilly.common.ui

import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import platform.UIKit.UIViewController

public interface NativeViewFactory {
    public fun createISSMapView(viewModel: ISSPositionViewModel): UIViewController
}