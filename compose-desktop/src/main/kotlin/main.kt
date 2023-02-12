import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.seiko.imageloader.rememberAsyncImagePainter
import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi

private val koin = initKoin(enableNetworkLogs = true).koin

fun main() = application {
    val windowState = rememberWindowState()

    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }
    var selectedPerson by remember { mutableStateOf<Assignment?>(null) }

    val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()

    LaunchedEffect(true) {
        peopleState = peopleInSpaceApi.fetchPeople().people
        selectedPerson = peopleState.first()
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "People In Space"
    ) {

        Row(Modifier.fillMaxSize()) {

            Box(Modifier.width(250.dp).fillMaxHeight().background(color = Color.LightGray)) {
                PersonList(peopleState, selectedPerson) {
                    selectedPerson = it
                }
            }

            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())

            Box(Modifier.fillMaxHeight()) {
                selectedPerson?.let {
                    PersonDetailsView(it)
                }
            }
        }

    }
}

@Composable
fun PersonList(
    people: List<Assignment>,
    selectedPerson: Assignment?,
    personSelected: (person: Assignment) -> Unit
) {

    // workaround for compose desktop but if LazyColumn is empty
    if (people.isNotEmpty()) {
        LazyColumn {
            items(people) { person ->
                PersonView(person, selectedPerson, personSelected)
            }
        }
    }
}

@Composable
fun PersonView(
    person: Assignment,
    selectedPerson: Assignment?,
    personSelected: (person: Assignment) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = { personSelected(person) })
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                person.name,
                style = if (person.name == selectedPerson?.name) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
            )

            Text(text = person.craft, style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
        }
    }
}

@Composable
fun PersonDetailsView(person: Assignment) {
    LazyColumn(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item(person) {

            Text(person.name, style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.size(12.dp))

            val personImageUrl = person.personImageUrl
            personImageUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(personImageUrl),
                    modifier = Modifier.size(240.dp), contentDescription = person.name
                )
            }
            Spacer(modifier = Modifier.size(24.dp))

            val bio = person.personBio ?: ""
            Text(bio, style = MaterialTheme.typography.body1)
        }
    }
}
