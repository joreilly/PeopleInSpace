package dev.johnoreilly.peopleinspace.peopleinspace.glance

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat.getDrawable
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import dev.johnoreilly.peopleinspace.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
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
        getDrawable(context, R.drawable.ic_iss)
    )

    val source = TileSourceFactory.DEFAULT_TILE_SOURCE
    val projection = Projection(zoomLevel, pWidth, pHeight, issPositionPoint, 0f, true, false, 0, 0)

    Configuration.getInstance().load(
        context.applicationContext,
        context.getSharedPreferences("osmdroid", MODE_PRIVATE)
    )

    val mapTileProvider = MapTileProviderBasic(context, source, null)
    try {
        val bitmap = withContext(Dispatchers.Main) {
            suspendCoroutine { cont ->
                val mapSnapshot = MapSnapshot(
                    {
                        if (it.status == MapSnapshot.Status.CANVAS_OK) {
                            val bitmap = Bitmap.createBitmap(it.bitmap)
                            cont.resume(bitmap)
                        }
                    },
                    MapSnapshot.INCLUDE_FLAG_UPTODATE or MapSnapshot.INCLUDE_FLAG_SCALED,
                    mapTileProvider,
                    if (includeStationMarker) listOf(stationMarker) else listOf(),
                    projection
                )

                launch(Dispatchers.IO) {
                    mapSnapshot.run()
                }
            }
        }
        return bitmap.asImageBitmap()
    } finally {
        mapTileProvider.detach()
    }
}