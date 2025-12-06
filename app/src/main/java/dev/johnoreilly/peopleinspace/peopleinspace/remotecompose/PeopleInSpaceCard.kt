@file:SuppressLint("RestrictedApi")

package dev.johnoreilly.peopleinspace.peopleinspace.remotecompose

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteOffset
import androidx.compose.remote.creation.compose.layout.rotate
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.background
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.tooling.preview.RemotePreview
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import dev.johnoreilly.peopleinspace.R
import org.osmdroid.util.GeoPoint

@RemoteComposable
@Composable
fun PeopleInSpaceCard(map: ImageBitmap, issPosition: GeoPoint) {
    val issVectorDrawable = issVectorDrawable()
    RemoteBox(
        modifier = RemoteModifier.fillMaxSize().background(Color.DarkGray)
    ) {
        val bitmap = issVectorDrawable
            .apply {
                setTint(Color.Black.toArgb())
            }
            .toBitmap(256, 256)

        RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
            val mapBitmapId = canvas.document.addBitmap(map.asAndroidBitmap())
            canvas.document.drawBitmap(
                mapBitmapId,
                0f,
                0f,
                remote.component.width.id,
                remote.component.height.id,
                null
            )

            val centerX = remote.component.centerX
            val centerY = remote.component.centerY
            drawCircle(Color.White.copy(alpha = 0.3f), radius = 48f, RemoteOffset(centerX, centerY))
            drawCircle(Color.Black, radius = 48f, RemoteOffset(centerX, centerY), 1f, Stroke(1f))

            val issBitmapId = canvas.document.addBitmap(bitmap)
            val angle = remote.time.ContinuousSec() * 10f % 360f
            rotate(angle, centerX, centerY) {
                canvas.document.drawBitmap(
                    issBitmapId,
                    (centerX - 32f).id,
                    (centerY - 32f).id,
                    (centerX + 32f).id,
                    (centerY + 32f).id,
                    null
                )
            }
        }
    }
}

@Composable
private fun issVectorDrawable(): VectorDrawableCompat {
    val drawable = VectorDrawableCompat.create(
        LocalResources.current, R.drawable.ic_iss,
        LocalContext.current.theme
    )!!
    return drawable
}

@Composable
@Preview(widthDp = 200, heightDp = 100)
fun PeopleInSpaceCardPreview() {
    RemotePreview {
        val previewMap =
            BitmapFactory.decodeResource(LocalResources.current, R.drawable.anfield).asImageBitmap()
        PeopleInSpaceCard(previewMap, GeoPoint(0.0, 0.0))
    }
}
