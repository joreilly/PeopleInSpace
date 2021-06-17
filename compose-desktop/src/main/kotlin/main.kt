import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surrus.common.di.initKoin
import com.surrus.common.model.personBios
import com.surrus.common.model.personImages
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skija.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

private val koin = initKoin(enableNetworkLogs = true).koin

fun main() = Window {
    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }
    var selectedPerson by remember { mutableStateOf("") }

    val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()

    LaunchedEffect(true) {
        peopleState = peopleInSpaceApi.fetchPeople().people
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("People In Space") })
            }) {

                Row(Modifier.fillMaxSize()) {

                    Box(Modifier.width(250.dp).fillMaxHeight().background(color = Color.LightGray))  {
                        PersonList(peopleState, selectedPerson) {
                            selectedPerson = it.name
                        }
                    }

                    Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())

                    Box(Modifier.fillMaxHeight()) {
                        PersonDetailsView(selectedPerson)
                    }
                }
            }
    }
}


@Composable
fun PersonList(people: List<Assignment>, selectedPerson: String, personSelected : (person : Assignment) -> Unit) {

    LazyColumn {
        items(people) { person ->
            PersonView(person, selectedPerson, personSelected)
        }
    }

}

@Composable
fun PersonView(person: Assignment, selectedPerson: String, personSelected : (person : Assignment) -> Unit) {
    Row(
        modifier =  Modifier.fillMaxWidth().clickable(onClick = { personSelected(person) })
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(person.name,
                style = if (person.name == selectedPerson) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
            )

            Text(text = person.craft, style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
        }
    }
}


@Composable
fun PersonDetailsView(personName: String) {
    LazyColumn(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item(personName) {

            Text(personName, style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.size(12.dp))

            val imageUrl = personImages[personName]
            imageUrl?.let {
                val imageAsset = fetchImage(it)
                imageAsset?.let {
                    Image(
                        it,
                        contentDescription = "personName",
                        modifier = Modifier.size(240.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(24.dp))

            val bio = personBios[personName] ?: ""
            Text(bio, style = MaterialTheme.typography.body1)
        }
    }
}


@Composable
fun fetchImage(url: String): ImageBitmap? {
    var image by remember(url) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        loadFullImage(url)?.let {
            image = Image.makeFromEncoded(toByteArray(it)).asImageBitmap()
        }
    }

    return image
}

fun toByteArray(bitmap: BufferedImage) : ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}

suspend fun loadFullImage(source: String): BufferedImage? = withContext(Dispatchers.IO) {
    runCatching {
        val url = URL(source)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        val input: InputStream = connection.inputStream
        val bitmap: BufferedImage? = ImageIO.read(input)
        bitmap
    }.getOrNull()
}

