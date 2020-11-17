import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surrus.common.model.personBios
import com.surrus.common.model.personImages
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi
import org.jetbrains.skija.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO


fun main() = Window {
    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }
    var selectedPerson by remember { mutableStateOf("") }
    val peopleInSpaceApi = PeopleInSpaceApi()

    launchInComposition {
        peopleState = peopleInSpaceApi.fetchPeople().people
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("People In Space") })
            },
            bodyContent = {

                Row(Modifier.fillMaxSize()) {

                    Box(Modifier.width(250.dp).fillMaxHeight(),
                        backgroundColor = Color.LightGray)
                    {
                        PersonList(peopleState) {
                            selectedPerson = it.name
                        }
                    }

                    Spacer(modifier = Modifier.preferredWidth(1.dp).fillMaxHeight())

                    Box(Modifier.fillMaxHeight()) {
                        PersonDetailsView(selectedPerson)
                    }
                }
            }
        )

    }
}


@Composable
fun PersonList(people: List<Assignment>, personSelected : (person : Assignment) -> Unit) {

    LazyColumnFor(items = people, itemContent = { person ->
        PersonView(person, personSelected)
    })
}

@Composable
fun PersonView(person: Assignment, personSelected : (person : Assignment) -> Unit) {
    Row(
        modifier =  Modifier.fillMaxWidth() + Modifier.clickable(onClick = { personSelected(person) })
                + Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(text = person.name, style = TextStyle(fontSize = 18.sp))
            Text(text = person.craft, style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
        }
    }
}


@Composable
fun PersonDetailsView(personName: String) {
    ScrollableColumn(modifier = Modifier.padding(16.dp) + Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(personName, style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.preferredSize(12.dp))

        val imageUrl = personImages[personName]
        imageUrl?.let {
            val imageAsset = fetchImage(it)
            imageAsset?.let {
                Image(it, modifier = Modifier.preferredSize(240.dp))
            }
        }
        Spacer(modifier = Modifier.preferredSize(24.dp))

        val bio = personBios[personName] ?: ""
        Text(bio, style = MaterialTheme.typography.body1)
    }
}


@Composable
fun fetchImage(url: String): ImageAsset? {
    var image by remember(url) { mutableStateOf<ImageAsset?>(null) }

    launchInComposition(url) {
        loadFullImage(url)?.let {
            image = Image.makeFromEncoded(toByteArray(it)).asImageAsset()
        }
    }

    return image
}

fun toByteArray(bitmap: BufferedImage) : ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}

fun loadFullImage(source: String): BufferedImage? {
    return try {
        val url = URL(source)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        val input: InputStream = connection.inputStream
        val bitmap: BufferedImage? = ImageIO.read(input)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

