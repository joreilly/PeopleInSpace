import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Bitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.*

val imagesCache = mutableMapOf<String, ImageBitmap>()

@Composable
fun WebAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier
) {

    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }

    if (bitmap != null) {
        Image(bitmap!!, contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.Crop)
    }

    LaunchedEffect(imageUrl) {
        if (imagesCache.contains(imageUrl)) {
            bitmap = imagesCache[imageUrl]!!
        } else {
            val arrayBuffer = loadImage(imageUrl)
            val skiaImg = org.jetbrains.skia.Image.makeFromEncoded(arrayBuffer.toByteArray())
            // Bitmap.makeFromImage(skiaImg)
            imagesCache[imageUrl] = skiaImg.toComposeImageBitmap()
            bitmap = imagesCache[imageUrl]
        }
    }
}
