import androidx.compose.desktop.Window
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi

fun main() = Window {
    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }
    val peopleInSpaceApi = PeopleInSpaceApi()

    launchInComposition {
        peopleState = peopleInSpaceApi.fetchPeople().people
    }

    MaterialTheme {
        PersonList(peopleState) {

        }
    }
}


@Composable
fun PersonList(people: List<Assignment>, personSelected : (person : Assignment) -> Unit) {

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("People In Space") })
        },
        bodyContent = {
            LazyColumnFor(items = people, itemContent = { person ->
                PersonView(person, personSelected)
            })
        }
    )
}

@Composable
fun PersonView(person: Assignment, personSelected : (person : Assignment) -> Unit) {
    Row(
        modifier =  Modifier.fillMaxWidth() + Modifier.clickable(onClick = { personSelected(person) })
                + Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(text = person.name, style = TextStyle(fontSize = 20.sp))
            Text(text = person.craft, style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
        }
    }
}