package dev.johnoreilly.peopleinspace.peopleinspace.remotecompose

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.remote.creation.profile.Profile
import androidx.compose.remote.creation.profile.RcPlatformProfiles
import androidx.compose.ui.graphics.ImageBitmap
import dev.johnoreilly.peopleinspace.peopleinspace.glance.fetchIssPosition
import dev.johnoreilly.peopleinspace.peopleinspace.glance.fetchMapBitmap
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.AsyncAppWidgetReceiver
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.RemoteComposeRecorder
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okio.ByteString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.osmdroid.util.GeoPoint

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@SuppressLint("RestrictedApi")
class PeopleInSpaceWidgetReceiver : AsyncAppWidgetReceiver(), KoinComponent {
    private val repository: PeopleInSpaceRepositoryInterface by inject()

    /** Called when widgets must provide remote views. */

    override suspend fun update(context: Context, wm: AppWidgetManager, widgetIds: IntArray) {
        // receiver context is restricted
        val appContext = context.applicationContext

        val issPosition = fetchIssPosition(repository)

        coroutineScope {
            widgetIds.forEach { widgetId ->
                launch {
                    val bitmap = fetchMapBitmap(
                        issPosition,
                        appContext,
                        includeStationMarker = false,
                        zoomLevel = 3.0,
                        pWidth = 400,
                        pHeight = 400
                    )

                    val bytes = recordPeopleInSpaceCard(
                        profile = RcPlatformProfiles.WIDGETS_V6,
                        recorder = RemoteComposeRecorder(appContext),
                        issPosition = issPosition,
                        map = bitmap
                    )

                    val widget = RemoteViews(DrawInstructions(bytes))

                    wm.updateAppWidget(widgetId, widget)
                }
            }
        }
    }

    private fun DrawInstructions(bytes: ByteString): RemoteViews.DrawInstructions {
        return RemoteViews.DrawInstructions.Builder(listOf(bytes.toByteArray())).build()
    }

    suspend fun recordPeopleInSpaceCard(
        recorder: RemoteComposeRecorder,
        profile: Profile,
        issPosition: GeoPoint,
        map: ImageBitmap,
    ): ByteString {
        return recorder.record(profile) { PeopleInSpaceCard(map, issPosition) }
    }
}