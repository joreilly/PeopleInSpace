@file:SuppressLint("RestrictedApi")

package dev.johnoreilly.peopleinspace.peopleinspace.remotecompose

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.remote.core.operations.utilities.ImageScaling
import androidx.compose.remote.creation.compose.capture.withTransform
import androidx.compose.remote.creation.compose.layout.RemoteCanvas
import androidx.compose.remote.creation.compose.layout.RemoteCanvasDrawScope
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteOffset
import androidx.compose.remote.creation.compose.layout.RemoteSize
import androidx.compose.remote.creation.compose.layout.rotate
import androidx.compose.remote.creation.compose.layout.translate
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.shaders.RemoteBrush
import androidx.compose.remote.creation.compose.shaders.radialGradient
import androidx.compose.remote.creation.compose.state.RemoteFloat
import androidx.compose.remote.creation.compose.state.RemoteLong
import androidx.compose.remote.creation.compose.state.deltaFromReferenceInSeconds
import androidx.compose.remote.creation.compose.state.max
import androidx.compose.remote.creation.compose.state.rf
import androidx.compose.remote.tooling.preview.RemotePreview
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import dev.johnoreilly.peopleinspace.R
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.MapResult
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.evaluateTime

/**
 * A Remote Compose card that displays a map with the ISS location and its projected orbit path.
 *
 * This composable uses [RemoteCanvas] to render a map bitmap and an animated ISS icon. The ISS
 * icon's position is interpolated based on the current time using [evaluateTime].
 *
 * @param mapResult The data containing the map [ImageBitmap] and the path translation data.
 */
@RemoteComposable
@Composable
fun PeopleInSpaceCard(mapResult: MapResult) {
    val issIcon = issVectorDrawable().apply { setTint(Color.Black.toArgb()) }.toBitmap(96, 96)

    val time = -deltaFromReferenceInSeconds(RemoteLong(mapResult.pathSegments.first().first * 1000))
    // Calculate the interpolated X and Y positions of the ISS for the current time.
    val issX = evaluateTime(mapResult, time) { _, offset -> offset.x.rf }
    val issY = evaluateTime(mapResult, time) { _, offset -> offset.y.rf }

    RemoteCanvas(modifier = RemoteModifier.fillMaxSize()) {
        // Calculate the scale and offset needed to fit the map bitmap centered in the widget.
        val (scale, xOffset, yOffset) = calculateScalingAndOffset(mapResult.bitmap)

        withTransform(
                transformBlock = {
                    // Apply translation and scaling to center the map.
                    translate(xOffset, yOffset)
                    scale(scale.id, scale.id, Offset(0f, 0f))
                }
        ) {
            val bitmap = mapResult.bitmap
            canvas.drawScaledBitmap(
                    bitmap.asAndroidBitmap(),
                    0f.rf,
                    0f.rf,
                    bitmap.width.rf,
                    bitmap.height.rf,
                    0.rf,
                    0.rf,
                    bitmap.width.rf,
                    bitmap.height.rf,
                    ImageScaling.SCALE_FIXED_SCALE,
                    1.rf,
                    null
            )

            val scrimColor = Color.White.copy(alpha = 0.7f)
            // Draw a soft radial gradient behind the ISS icon to make it stand out from the map.
            this@RemoteCanvas.drawRoundRect(
                    brush =
                            RemoteBrush.radialGradient(
                                    0f to scrimColor,
                                    0.6f to scrimColor,
                                    1f to Color.Transparent,
                                    radius = 48f.rf
                            ),
                    topLeft = RemoteOffset(issX - 48.rf, issY - 48.rf),
                    size = RemoteSize(96.rf, 96.rf),
                    cornerRadius = CornerRadius(48.rf.id, 48.rf.id)
            )

            val issBitmapId = canvas.document.addBitmap(issIcon)
            // Animate the rotation of the ISS icon.
            val angle = remote.time.ContinuousSec() * 10f % 360f
            this@RemoteCanvas.rotate(angle, issX, issY) {
                // Draw the ISS icon at the current interpolated position.
                canvas.document.drawBitmap(
                        issBitmapId,
                        (issX - 32f).id,
                        (issY - 32f).id,
                        (issX + 32f).id,
                        (issY + 32f).id,
                        null
                )
            }
        }
    }
}

/**
 * Calculates the scale and translation needed to fit the map bitmap into the available canvas size.
 * It ensures the map is centered and fulfills the widget's dimensions, handling potential aspect
 * ratio differences.
 */
private fun RemoteCanvasDrawScope.calculateScalingAndOffset(
        bitmap: ImageBitmap
): Triple<RemoteFloat, RemoteFloat, RemoteFloat> {
    val widgetWidth = remote.component.width
    val widgetHeight = remote.component.height

    // Determine the scale factor for both axes to fit the bitmap.
    val widthScale = widgetWidth / bitmap.width.rf
    val heightScale = widgetHeight / bitmap.height.rf

    // Use the larger scale to ensure the entire background is covered (fill center).
    val scale = max(max(widthScale, heightScale), 1.rf)

    // Calculate offsets to center the scaled bitmap within the available space.
    val xOffset = (widgetWidth - bitmap.width.rf * scale) / 2.rf
    val yOffset = (widgetHeight - bitmap.height.rf * scale) / 2.rf
    return Triple(scale, xOffset, yOffset)
}

@Composable
private fun issVectorDrawable(): VectorDrawableCompat {
    val drawable =
            VectorDrawableCompat.create(
                    LocalResources.current,
                    R.drawable.ic_iss,
                    LocalContext.current.theme
            )!!
    return drawable
}

@Composable
@Preview(widthDp = 200, heightDp = 100)
fun PeopleInSpaceCardPreview() {
    RemotePreview {
        val previewMap =
                BitmapFactory.decodeResource(LocalResources.current, R.drawable.anfield)
                        .asImageBitmap()
        PeopleInSpaceCard(MapResult(previewMap, listOf(0.toLong() to Offset(0f, 0f))))
    }
}
