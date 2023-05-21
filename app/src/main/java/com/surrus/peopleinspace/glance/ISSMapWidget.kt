package com.surrus.peopleinspace.glance

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.surrus.common.remote.IssPosition
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.peopleinspace.MainActivity
import com.surrus.peopleinspace.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.Projection
import org.osmdroid.views.drawing.MapSnapshot
import org.osmdroid.views.overlay.IconOverlay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ISSMapWidget: GlanceAppWidget(), KoinComponent {
    private val repository: PeopleInSpaceRepositoryInterface by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val issPosition: IssPosition = withContext(Dispatchers.Main) {
            repository.pollISSPosition().first()
        }

        val issPositionPoint = GeoPoint(issPosition.latitude, issPosition.longitude)

        val stationMarker = IconOverlay(
            issPositionPoint,
            context.resources.getDrawable(R.drawable.ic_iss, context.theme)
        )

        val source = TileSourceFactory.DEFAULT_TILE_SOURCE
        val projection = Projection(5.0, 480, 240, issPositionPoint, 0f, true, false, 0, 0)

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
                    listOf(stationMarker),
                    projection
                )

                launch(Dispatchers.IO) {
                    mapSnapshot.run()
                }
            }
        }

        provideContent {
            Box(
                modifier = GlanceModifier.background(Color.DarkGray).fillMaxSize().clickable(
                    actionStartActivity<MainActivity>()
                )
            ) {
                Image(
                    modifier = GlanceModifier.fillMaxSize(),
                    provider = ImageProvider(bitmap),
                    contentDescription = "ISS Location"
                )
            }
        }
    }
}