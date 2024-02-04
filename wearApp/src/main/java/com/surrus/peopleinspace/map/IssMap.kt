package com.surrus.peopleinspace.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.surrus.common.remote.IssPosition
import com.surrus.peopleinspace.R
import com.utsman.osmandcompose.MapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun IssMapScreen(
    modifier: Modifier = Modifier,
) {
    val peopleInSpaceViewModel = koinViewModel<MapViewModel>()
    val issPosition by peopleInSpaceViewModel.issPosition.collectAsStateWithLifecycle()

    IssMap(modifier, issPosition = issPosition ?: IssPosition(0.0, 0.0))
}

@Composable
private fun IssMap(
    modifier: Modifier,
    issPosition: IssPosition?
) {
    val context = LocalContext.current

    val geoPointState by remember {
        derivedStateOf {
            if (issPosition != null) {
                GeoPoint(issPosition.latitude, issPosition.longitude)
            } else {
                GeoPoint(0.0, 0.0)
            }
        }
    }

    val cameraState = rememberCameraState {
        geoPoint = geoPointState
        zoom = 2.0
    }

    val issMarkerState =
        rememberMarkerState(
            geoPoint = geoPointState,
            rotation = 90f,
        )

    val issIcon = remember {
        ContextCompat.getDrawable(context, R.drawable.ic_iss)
    }

    OpenStreetMap(
        modifier = modifier.fillMaxSize(),
        cameraState = cameraState,
        properties = MapProperties(
            isMultiTouchControls = false,
            isAnimating = true,
            isFlingEnable = false,
            zoomButtonVisibility = ZoomButtonVisibility.SHOW_AND_FADEOUT
        )
    ) {
        if (issPosition != null) {
            Marker(state = issMarkerState, icon = issIcon, title = "ISS")
        }
    }
}