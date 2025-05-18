import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.common.repository.PeopleInSpaceRepository

private val koin = initKoin(enableNetworkLogs = true).koin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    val peopleInSpaceRepository = koin.get<PeopleInSpaceRepository>()

    CanvasBasedWindow("PeopleInSpace", canvasElementId = "peopleInSpaceCanvas") {

        val people by peopleInSpaceRepository.fetchPeopleAsFlow().collectAsState(emptyList())

        Column {
            Text(
                text ="PeopleInSpace",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall)

            //PeopleInSpaceScreen()


            PersonList(people, null, {})
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
            Text(person.name,)
            Text(text = person.craft)
        }
    }
}
