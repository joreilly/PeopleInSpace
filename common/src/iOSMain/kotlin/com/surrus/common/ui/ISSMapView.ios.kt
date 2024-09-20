package com.surrus.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import com.surrus.common.viewmodel.ISSPositionViewModel
import kotlinx.cinterop.ExperimentalForeignApi

@Composable
actual fun ISSMapView(modifier: Modifier, viewModel: ISSPositionViewModel) {
    MapKitView(
        modifier = modifier,
        viewModel = viewModel,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun MapKitView(
    modifier: Modifier,
    viewModel: ISSPositionViewModel
) {
    val factory = LocalNativeViewFactory.current

    UIKitViewController(
        factory = {
            factory.createISSMapView(viewModel)
        },
        modifier = modifier,
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.NonCooperative,
            isNativeAccessibilityEnabled = true
        )
    )
}