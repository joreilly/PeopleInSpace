package com.surrus.common.ui

import com.surrus.common.remote.IssPosition
import com.surrus.common.viewmodel.ISSPositionViewModel
import platform.UIKit.UIViewController

interface NativeViewFactory {
    fun createISSMapView(
        viewModel: ISSPositionViewModel
    ): UIViewController
}