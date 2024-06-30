package com.surrus.common.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.ComposeUIViewController
import com.surrus.common.viewmodel.ISSPositionViewModel


val LocalNativeViewFactory = staticCompositionLocalOf<NativeViewFactory> {
    error("LocalNativeViewFactory not provided")
}

object SharedViewControllers {
    fun ISSPositionContentViewController(viewModel: ISSPositionViewModel, nativeViewFactory: NativeViewFactory) = ComposeUIViewController {
        CompositionLocalProvider(LocalNativeViewFactory provides nativeViewFactory) {
            ISSPositionContent(viewModel)
        }
    }
}