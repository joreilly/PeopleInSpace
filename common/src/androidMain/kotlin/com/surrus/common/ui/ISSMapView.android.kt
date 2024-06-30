package com.surrus.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.surrus.common.viewmodel.ISSPositionViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
actual fun ISSMapView(modifier: Modifier, viewModel: ISSPositionViewModel) {
    val position by viewModel.position.collectAsState()

    val context = LocalContext.current
    val map = remember {
        MapView(context).apply {
            clipToOutline = true
        }
    }

    AndroidView({ map }, modifier = modifier) { map ->
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(4.0)
        val issPositionPoint = GeoPoint(position.latitude, position.longitude)
        mapController.setCenter(issPositionPoint)

        map.overlays.clear()
        val stationMarker = Marker(map)
        stationMarker.position = issPositionPoint
        stationMarker.title = "ISS"
        map.overlays.add(stationMarker)
    }
}