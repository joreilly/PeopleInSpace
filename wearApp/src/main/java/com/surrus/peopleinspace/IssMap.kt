package com.surrus.peopleinspace

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import com.surrus.common.remote.IssPosition
import org.koin.androidx.compose.getViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

const val ISSPositionMapTag = "ISSPositionMap"

val IssPositionKey = SemanticsPropertyKey<IssPosition>("IssPosition")
var SemanticsPropertyReceiver.observedIssPosition by IssPositionKey

@Composable
fun IssMap() {
    val context = LocalContext.current

    org.osmdroid.config.Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    org.osmdroid.config.Configuration.getInstance().tileFileSystemCacheMaxBytes = 50L * 1024 * 1024
    org.osmdroid.config.Configuration.getInstance().osmdroidTileCache =
        File(context.cacheDir, "osmdroid").also { it.mkdir() }

    val peopleInSpaceViewModel = getViewModel<PeopleInSpaceViewModel>()

    val issPosition by peopleInSpaceViewModel.issPosition
        .collectAsState(IssPosition(0.0, 0.0))

    val mapView = remember { MapView(context) }

    MaterialTheme {
        Scaffold {
            AndroidView(
                factory = {
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK);
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                        setMultiTouchControls(false)
                        controller.setZoom(3.0)
                        // disable movement
                        setClickable(false)
                        isFocusable = false
                        setOnTouchListener { v, event -> true }
                    }
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .testTag(ISSPositionMapTag)
                    .semantics { observedIssPosition = issPosition },
                update = { map ->
                    println(issPosition)
                    val issPositionPoint = GeoPoint(issPosition.latitude, issPosition.longitude)
                    map.controller.setCenter(issPositionPoint)

                    map.overlays.clear()
                    val stationMarker = Marker(map)
                    stationMarker.position = issPositionPoint
                    stationMarker.title = "ISS"
                    map.overlays.add(stationMarker)
                }
            )
        }
    }
}