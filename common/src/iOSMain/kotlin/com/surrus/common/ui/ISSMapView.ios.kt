package com.surrus.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import com.surrus.common.viewmodel.ISSPositionViewModel
import kotlinx.cinterop.ExperimentalForeignApi

@Composable
actual fun ISSMapView(modifier: Modifier, viewModel: ISSPositionViewModel) {
    MapKitView(
        modifier = modifier,
        viewModel = viewModel,
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun MapKitView(
    modifier: Modifier,
    viewModel: ISSPositionViewModel
) {
    val factory = LocalNativeViewFactory.current

    UIKitViewController(
        modifier = modifier,
        factory = {
            factory.createISSMapView(viewModel)
        }
    )
}