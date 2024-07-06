package com.surrus.common.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.surrus.common.viewmodel.ISSPositionViewModel
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import org.osmdroid.util.GeoPoint


@Composable
actual fun ISSMapView(modifier: Modifier, viewModel: ISSPositionViewModel) {
    val position by viewModel.position.collectAsState()

    val cameraState by remember {
        derivedStateOf {
            CameraState(CameraProperty().apply {
                geoPoint = GeoPoint(position.latitude, position.longitude)
                zoom = 4.0
            })
        }
    }

    val issPositionMarkerState by remember {
        derivedStateOf {
            val geoPoint = GeoPoint(position.latitude, position.longitude)
            MarkerState(geoPoint, 0.0f)
        }
    }

    Surface(modifier = modifier.fillMaxSize(),) {
        OpenStreetMap(
            cameraState = cameraState,
            properties = DefaultMapProperties.copy(minZoomLevel = 4.0),
        ) {
            Marker(state = issPositionMarkerState, title = "ISS")
        }
    }
}