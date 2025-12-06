package dev.johnoreilly.peopleinspace.glance

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
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
import dev.johnoreilly.peopleinspace.peopleinspace.glance.fetchIssPosition
import dev.johnoreilly.peopleinspace.peopleinspace.glance.fetchMapBitmap
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import dev.johnoreilly.peopleinspace.MainActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ISSMapWidget: GlanceAppWidget(), KoinComponent {
    private val repository: PeopleInSpaceRepositoryInterface by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val issPositionPoint = fetchIssPosition(repository)

        val bitmap = fetchMapBitmap(issPositionPoint, context)

        provideContent {
            Box(
                modifier = GlanceModifier.background(Color.DarkGray).fillMaxSize().clickable(
                    actionStartActivity<MainActivity>()
                )
            ) {
                Image(
                    modifier = GlanceModifier.fillMaxSize(),
                    provider = ImageProvider(bitmap.asAndroidBitmap()),
                    contentDescription = "ISS Location"
                )
            }
        }
    }
}