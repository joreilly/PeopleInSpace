package dev.johnoreilly.peopleinspace.peopleinspace.remotecompose

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.remote.creation.compose.capture.captureSingleRemoteDocument
import androidx.compose.remote.creation.profile.RcPlatformProfiles
import androidx.compose.ui.geometry.Size
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.AsyncAppWidgetReceiver
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.fetchMapBitmapInRange
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.filterRecent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.ByteString
import okio.ByteString.Companion.toByteString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@SuppressLint("RestrictedApi")
class PeopleInSpaceWidgetReceiver : AsyncAppWidgetReceiver(), KoinComponent {
    private val repository: PeopleInSpaceRepositoryInterface by inject()

    /** Called when widgets must provide remote views. */
    override suspend fun update(context: Context, wm: AppWidgetManager, widgetIds: IntArray) {
        // receiver context is restricted
        val appContext = context.applicationContext

        coroutineScope {
            widgetIds.forEach { widgetId ->
                launch {
                    val imageSizePx = imageSize(wm, widgetId, context)

                    val mapResult =
                            fetchMapBitmapInRange(
                                    points = filterRecent(repository.fetchISSFuturePosition()),
                                    context = appContext,
                                    size = imageSizePx,
                            )

                    // Capture the Remote Compose document as a byte array.
                    val bytes =
                            withContext(Dispatchers.Main) {
                                captureSingleRemoteDocument(
                                        context = context,
                                        profile = RcPlatformProfiles.WIDGETS_V6
                                ) { PeopleInSpaceCard(mapResult) }
                            }
                    // Wrap the captured document bytes into a RemoteViews.DrawInstructions object.
                    val widget = RemoteViews(DrawInstructions(bytes.bytes.toByteString()))

                    wm.updateAppWidget(widgetId, widget)
                }
            }
        }
    }

    private fun imageSize(wm: AppWidgetManager, widgetId: Int, context: Context): Size {
        val options = wm.getAppWidgetOptions(widgetId)
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val density = context.resources.displayMetrics.density
        val imageSizePx = Size(minWidth * density, minHeight * density)
        return imageSizePx
    }

    private fun DrawInstructions(bytes: ByteString): RemoteViews.DrawInstructions {
        return RemoteViews.DrawInstructions.Builder(listOf(bytes.toByteArray())).build()
    }
}
