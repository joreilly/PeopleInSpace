package com.surrus.peopleinspace.glance

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import dev.johnoreilly.peopleinspace.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.Projection
import org.osmdroid.views.drawing.MapSnapshot
import org.osmdroid.views.overlay.IconOverlay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun fetchIssPosition(repository: PeopleInSpaceRepositoryInterface): GeoPoint {
    val issPosition: IssPosition = repository.pollISSPosition().first()

    val issPositionPoint = GeoPoint(issPosition.latitude, issPosition.longitude)
    println("ISS Position: $issPositionPoint")
    return issPositionPoint
}

suspend fun fetchMapBitmap(
    issPositionPoint: GeoPoint,
    context: Context,
    includeStationMarker: Boolean = true,
    zoomLevel: Double = 1.0,
    pWidth: Int = 480,
    pHeight: Int = 240,
): ImageBitmap {
    val stationMarker = IconOverlay(
        issPositionPoint,
        context.resources.getDrawable(R.drawable.ic_iss, context.theme)
    )

    val source = TileSourceFactory.DEFAULT_TILE_SOURCE
    val projection = Projection(zoomLevel, pWidth, pHeight, issPositionPoint, 0f, true, false, 0, 0)

    val bitmap = withContext(Dispatchers.Main) {
        suspendCoroutine<Bitmap> { cont ->
            val mapSnapshot = MapSnapshot(
                {
                    if (it.status == MapSnapshot.Status.CANVAS_OK) {
                        val bitmap = Bitmap.createBitmap(it.bitmap)
                        cont.resume(bitmap)
                    }
                },
                MapSnapshot.INCLUDE_FLAG_UPTODATE or MapSnapshot.INCLUDE_FLAG_SCALED,
                MapTileProviderBasic(context, source, null),
                if (includeStationMarker) listOf(stationMarker) else listOf(),
                projection
            )

            launch(Dispatchers.IO) {
                mapSnapshot.run()
            }
        }
    }
    return bitmap.asImageBitmap()
}