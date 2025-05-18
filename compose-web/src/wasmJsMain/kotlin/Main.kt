import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.CanvasBasedWindow
import coil3.compose.AsyncImage
import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepository

private val koin = initKoin(enableNetworkLogs = true).koin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    val peopleInSpaceRepository = koin.get<PeopleInSpaceRepository>()

    CanvasBasedWindow("PeopleInSpace", canvasElementId = "peopleInSpaceCanvas") {

        val people by peopleInSpaceRepository.fetchPeopleAsFlow().collectAsState(emptyList())
        var selectedPerson by remember { mutableStateOf<Assignment?>(null) }


        Row(Modifier.fillMaxSize()) {

            Box(Modifier.width(250.dp).fillMaxHeight().background(color = Color.LightGray)) {
                PersonList(people, selectedPerson) {
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
                style = if (person.name == selectedPerson?.name) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
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
            Text(person.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.size(12.dp))

            val personImageUrl = person.personImageUrl
            personImageUrl?.let {
                AsyncImage(
                    modifier = Modifier.size(240.dp),
                    model = personImageUrl,
                    contentDescription = person.name
                )
            }
            Spacer(modifier = Modifier.size(24.dp))

            val bio = person.personBio ?: ""
            Text(bio, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
