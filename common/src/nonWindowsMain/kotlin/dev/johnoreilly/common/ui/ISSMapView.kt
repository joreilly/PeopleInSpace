package dev.johnoreilly.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.johnoreilly.common.viewmodel.ISSPositionViewModel

@Composable
expect fun ISSMapView(modifier: Modifier, viewModel: ISSPositionViewModel)
