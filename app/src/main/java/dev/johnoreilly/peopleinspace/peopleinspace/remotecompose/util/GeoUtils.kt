package dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.Point
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import dev.johnoreilly.common.remote.OrbitPoint
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.Projection
import org.osmdroid.views.drawing.MapSnapshot
import org.osmdroid.views.drawing.MapSnapshot.Status
import org.osmdroid.views.overlay.Polyline

data class MapResult(val bitmap: ImageBitmap, val pathSegments: List<Pair<Long, Offset>>)

fun OrbitPoint.toGeoPoint(): GeoPoint {
    return GeoPoint(this.lt, this.ln)
}

/**
 * Fetches a map bitmap from osmdroid that covers the bounding box of the given ISS orbit points.
 *
 * This function calculates the optimal center and zoom level to fit all provided [points] on the
 * map, accounting for the antimeridian (international dateline) wrapping. It also draws the orbit
 * path as a series of red [Polyline] segments.
 *
 * @param points A list of [OrbitPoint] representing the ISS path.
 * @param context The application context, used for osmdroid configuration and tile providers.
 * @param size The target size of the resulting bitmap in pixels.
 * @return A [MapResult] containing the generated [ImageBitmap] and a list of path segments
 *          mapped to timestamps and canvas [Offset]s.
 */
suspend fun fetchMapBitmapInRange(
        points: List<OrbitPoint>,
        context: Context,
        size: Size,
): MapResult {
    val geoPoints = points.map { it.toGeoPoint() }

    // Calculate the latitude range. Latitude doesn't wrap around the poles.
    val minLat: Double = geoPoints.minOf { it.latitude }
    val maxLat: Double = geoPoints.maxOf { it.latitude }

    val sortedLons = geoPoints.map { it.longitude }.sorted()
    var maxGap = 0.0
    var gapEnd = sortedLons.first()

    // Determine the smallest longitudinal span by finding the largest gap between consecutive
    // points.
    // This correctly handles the case where the orbit crosses the International Dateline
    // (antimeridian).
    if (sortedLons.size > 1) {
        // Initial gap accounts for the span wrapping from the last point to the first.
        maxGap = 360.0 - (sortedLons.last() - sortedLons.first())
        for (i in 0 until sortedLons.size - 1) {
            val gap = sortedLons[i + 1] - sortedLons[i]
            if (gap > maxGap) {
                maxGap = gap
                gapEnd = sortedLons[i + 1]
            }
        }
    }

    // Calculate the total longitudinal difference and the resulting center longitude.
    val lngDiff = 360.0 - maxGap
    val centerLng =
            if (lngDiff == 0.0) sortedLons.first()
            else {
                var c = gapEnd + (lngDiff / 2.0)
                // Normalize the center longitude to (-180, 180] range.
                while (c > 180.0) c -= 360.0
                while (c < -180.0) c += 360.0
                c
            }

    val centerLat = (minLat + maxLat) / 2.0
    val center = GeoPoint(centerLat, centerLng)

    val sidePadding = 30.0
    val latDiff = maxLat - minLat

    // Calculate zoom levels for both dimensions based on the pixel size and the geographic span.
    // 256 is the standard tile size for Web Mercator.
    val zoomX = log2(((size.width - 2 * sidePadding) * 360.0) / (256.0 * max(lngDiff, 0.1)))
    val zoomY = log2(((size.height - 2 * sidePadding) * 180.0) / (256.0 * max(latDiff, 0.1)))

    // Select the minimum zoom level to ensure all points fit within the requested area.
    val zoomLevel = min(zoomX, zoomY).coerceIn(1.0, 18.0)

    val projection =
            Projection(
                    zoomLevel,
                    size.width.toInt(),
                    size.height.toInt(),
                    center,
                    0f,
                    true,
                    false,
                    0,
                    0
            )

    Configuration.getInstance()
            .load(
                    context.applicationContext,
                    context.getSharedPreferences("osmdroid", MODE_PRIVATE)
            )

    val mapTileProvider = MapTileProviderBasic(context, TileSourceFactory.DEFAULT_TILE_SOURCE, null)

    // Split the list of points into segments where they cross the dateline.
    // This allows osmdroid's Polyline to draw separate lines instead of a single line wrapping
    // across the globe.
    val segments = mutableListOf<MutableList<GeoPoint>>()
    if (geoPoints.isNotEmpty()) {
        segments.add(mutableListOf(geoPoints.first()))
        for (i in 1 until geoPoints.size) {
            val prev = geoPoints[i - 1]
            val curr = geoPoints[i]
            // If the longitude jump is > 180, it's a dateline crossing.
            if (kotlin.math.abs(curr.longitude - prev.longitude) > 180.0) {
                segments.add(mutableListOf())
            }
            segments.last().add(curr)
        }
    }

    val pathSegments =
            points.map { op ->
                val gp = op.toGeoPoint()
                val point = Point()
                projection.toPixels(gp, point)
                Pair(op.t, Offset(point.x.toFloat(), point.y.toFloat()))
            }

    val overlays =
            segments.map { segment ->
                Polyline().apply {
                    setPoints(segment)
                    outlinePaint.color = Color.Red.toArgb()
                    outlinePaint.strokeWidth = 5f
                }
            }

    val bitmap: Bitmap =
            withContext(Dispatchers.Main) {
                suspendCoroutine { cont ->
                    val mapSnapshot =
                            MapSnapshot(
                                    { snapshot ->
                                        if (snapshot.status == Status.CANVAS_OK) {
                                            val b: Bitmap = snapshot.bitmap
                                            cont.resume(Bitmap.createBitmap(b))
                                        }
                                    },
                                    MapSnapshot.INCLUDE_FLAG_UPTODATE or
                                            MapSnapshot.INCLUDE_FLAG_SCALED,
                                    mapTileProvider,
                                    overlays,
                                    projection
                            )

                    // Start the snapshot generation on a background thread.
                    launch(Dispatchers.IO) { mapSnapshot.run() }
                }
            }
    return MapResult(bitmap.asImageBitmap(), pathSegments)
}

fun filterRecent(positions: List<OrbitPoint>): List<OrbitPoint> {
    val now = System.currentTimeMillis() / 1000
    // Keep a focused window (-5m to +15m)
    return positions.filter { it.t in now - 300..now + 900 }
}
