package com.surrus.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.surrus.common.viewmodel.ISSPositionViewModel

@Composable
expect fun ISSMapView(modifier: Modifier, viewModel: ISSPositionViewModel)
