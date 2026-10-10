package dev.johnoreilly.common.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import platform.UIKit.UIViewController
import androidx.compose.ui.window.ComposeUIViewController
import dev.johnoreilly.common.viewmodel.ISSPositionViewModel


internal val LocalNativeViewFactory: ProvidableCompositionLocal<NativeViewFactory> = staticCompositionLocalOf {
    error("LocalNativeViewFactory not provided")
}


public fun ISSPositionContentViewController(
    viewModel: ISSPositionViewModel,
    createISSMapView: (ISSPositionViewModel) -> UIViewController,
): UIViewController = ComposeUIViewController {
    CompositionLocalProvider(LocalNativeViewFactory provides NativeViewFactory(createISSMapView)) {
        ISSPositionContent(viewModel)
    }
}
