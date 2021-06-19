package com.surrus.peopleinspace.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.surrus.common.remote.IssPosition
import com.surrus.peopleinspace.util.collectAsStateWithLifecycle
import org.koin.androidx.compose.getViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ISSPositionScreen() {
    val peopleInSpaceViewModel = getViewModel<PeopleInSpaceViewModel>()

    val lifecycleOwner = LocalLifecycleOwner.current

    val issPosition by peopleInSpaceViewModel.issPosition
        .collectAsStateWithLifecycle(lifecycleOwner, IssPosition(0.0, 0.0))

    val context = LocalContext.current
    val map = remember { MapView(context) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ISS Position") })
        }
    ) {
        Column {

            Box {
                AndroidView({ map }, modifier = Modifier.fillMaxHeight()) { map ->
                    map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                    map.setMultiTouchControls(true)

                    val mapController = map.controller
                    mapController.setZoom(5.0)
                    val issPositionPoint = GeoPoint(issPosition.latitude, issPosition.longitude)
                    mapController.setCenter(issPositionPoint)

                    map.overlays.clear()
                    val stationMarker = Marker(map)
                    stationMarker.position = issPositionPoint
                    stationMarker.title = "ISS"
                    map.overlays.add(stationMarker)
                }
            }
        }
    }
}
