import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale.Companion.Crop

val imagesCache = mutableMapOf<String, ImageBitmap>()

@Composable
fun WebAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier,
) {
    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }
    bitmap?.let { b ->
        Image(
            bitmap = b,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = Crop,
        )
    }

    LaunchedEffect(imageUrl) {
        bitmap = imagesCache.getOrPut(imageUrl) {
            val arrayBuffer = loadImage(imageUrl)
            val skiaImg = org.jetbrains.skia.Image.makeFromEncoded(arrayBuffer.toByteArray())
            // Bitmap.makeFromImage(skiaImg)
            skiaImg.toComposeImageBitmap()
        }
    }
}
