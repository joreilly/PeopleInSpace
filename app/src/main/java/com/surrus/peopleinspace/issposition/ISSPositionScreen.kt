@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.surrus.peopleinspace.issposition

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.surrus.common.remote.IssPosition
import com.surrus.peopleinspace.R
import com.surrus.peopleinspace.ui.PeopleInSpaceTopAppBar
import org.koin.androidx.compose.getViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


const val ISSPositionMapTag = "ISSPositionMap"

val IssPositionKey = SemanticsPropertyKey<IssPosition>("IssPosition")
var SemanticsPropertyReceiver.observedIssPosition by IssPositionKey


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ISSPositionRoute(viewModel: ISSPositionViewModel = getViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ISSPositionScreen(uiState)
}

@Composable
fun ISSPositionScreen(uiState: ISSPositionUiState) {
    val context = LocalContext.current
    val map = remember {
        MapView(context).apply {
            clipToOutline = true
        }
    }

    Scaffold(
        topBar = {
            PeopleInSpaceTopAppBar(
                titleRes = R.string.iss_position,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.semantics { contentDescription = "ISSPosition" }
            )
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
            AndroidView({ map }, modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight().testTag(ISSPositionMapTag)
                    .semantics { observedIssPosition = uiState.position }
            ) { map ->
                map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                map.setMultiTouchControls(true)

                val mapController = map.controller
                mapController.setZoom(5.0)
                val issPositionPoint = GeoPoint(uiState.position.latitude, uiState.position.longitude)
                mapController.setCenter(issPositionPoint)

                map.overlays.clear()
                val stationMarker = Marker(map)
                stationMarker.position = issPositionPoint
                stationMarker.title = "ISS"
                map.overlays.add(stationMarker)
            }
    }
}
