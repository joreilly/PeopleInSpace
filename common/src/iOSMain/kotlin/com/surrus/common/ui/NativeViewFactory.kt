package dev.johnoreilly.common.ui

import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import platform.UIKit.UIViewController

interface NativeViewFactory {
    fun createISSMapView(viewModel: ISSPositionViewModel): UIViewController
}