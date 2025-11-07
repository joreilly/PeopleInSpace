package com.surrus.peopleinspace.remotecompose

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.remote.creation.profile.Profile
import androidx.compose.remote.creation.profile.RcPlatformProfiles
import com.surrus.peopleinspace.remotecompose.util.AsyncAppWidgetReceiver
import com.surrus.peopleinspace.remotecompose.util.RemoteComposeRecorder
import okio.ByteString

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@SuppressLint("RestrictedApi")
class RCMemberCardWidgetReceiver : AsyncAppWidgetReceiver() {
    /** Called when widgets must provide remote views. */

    override suspend fun update(context: Context, wm: AppWidgetManager, widgetIds: IntArray) {

        val bytes = recordPeopleInSpaceCard(
            profile = RcPlatformProfiles.WIDGETS_V6,
            recorder = RemoteComposeRecorder(context)
        )

        val widget =
            RemoteViews(RemoteViews.DrawInstructions.Builder(listOf(bytes.toByteArray())).build())

        widgetIds.forEach { widgetId -> wm.updateAppWidget(widgetId, widget) }
    }

    suspend fun recordPeopleInSpaceCard(
        recorder: RemoteComposeRecorder,
        profile: Profile
    ): ByteString {
        return recorder.record(profile) { PeopleInSpaceCard() }
    }
}