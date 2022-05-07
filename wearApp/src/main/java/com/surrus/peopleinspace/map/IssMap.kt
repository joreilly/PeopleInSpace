package com.surrus.peopleinspace.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import com.surrus.common.remote.IssPosition
import com.surrus.peopleinspace.BuildConfig
import com.surrus.peopleinspace.util.rememberStateWithLifecycle
import org.koin.androidx.compose.getViewModel
import org.osmdroid.config.Configuration
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
fun IssMapScreen(
    modifier: Modifier = Modifier,
) {
    val peopleInSpaceViewModel = getViewModel<MapViewModel>()
    val issPosition by rememberStateWithLifecycle(peopleInSpaceViewModel.issPosition)

    IssMap(modifier, issPosition)
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun IssMap(
    modifier: Modifier,
    issPosition: IssPosition?
) {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    Configuration.getInstance().tileFileSystemCacheMaxBytes = 50L * 1024 * 1024
    Configuration.getInstance().osmdroidTileCache =
        File(context.cacheDir, "osmdroid").also { it.mkdir() }

    val mapView = remember { MapView(context) }

    AndroidView(
        factory = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK);
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                setMultiTouchControls(false)
                controller.setZoom(3.0)
                isClickable = false
                isFocusable = false
                setOnTouchListener { _, _ -> true }
            }
        },
        modifier = modifier
            .fillMaxHeight()
            .testTag(ISSPositionMapTag)
            .semantics {
                if (issPosition != null) {
                    observedIssPosition = issPosition
                }
            },
        update = { map ->
            map.overlays.clear()

            if (issPosition != null) {
                val issPositionPoint = GeoPoint(issPosition.latitude, issPosition.longitude)
                map.controller.setCenter(issPositionPoint)

                val stationMarker = Marker(map)
                stationMarker.position = issPositionPoint
                stationMarker.title = "ISS"
                map.overlays.add(stationMarker)
            }
        }
    )
}