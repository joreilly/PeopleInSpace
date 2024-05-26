
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage


@Composable
fun PeopleInSpaceScreen() {
    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }
    var selectedPerson by remember { mutableStateOf<Assignment?>(null) }

    val peopleInSpaceApi = PeopleInSpaceApi()

    LaunchedEffect(true) {
        peopleState = peopleInSpaceApi.fetchPeople().people
        selectedPerson = peopleState.first()
    }

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

@Serializable
data class AstroResult(val message: String, val number: Int, val people: List<Assignment>)

@Serializable
data class Assignment(val craft: String, val name: String, var personImageUrl: String? = "", var personBio: String? = "")

@Serializable
data class IssPosition(val latitude: Double, val longitude: Double)

@Serializable
data class IssResponse(val message: String, val iss_position: IssPosition, val timestamp: Long)

class PeopleInSpaceApi(
    var baseUrl: String = "https://people-in-space-proxy.ew.r.appspot.com",
)  {

    val client = HttpClient() {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    }


    suspend fun fetchPeople() = client.get("$baseUrl/astros.json").body<AstroResult>()
    suspend fun fetchISSPosition() = client.get("$baseUrl/iss-now.json").body<IssResponse>()
}
