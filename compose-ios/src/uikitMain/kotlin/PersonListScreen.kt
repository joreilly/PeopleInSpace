@file:Suppress("FunctionName")

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface


@Composable
fun PersonListScreen(repo: PeopleInSpaceRepositoryInterface) {
    var people by remember { mutableStateOf(emptyList<Assignment>()) }

    LaunchedEffect(true) {
        people = repo.fetchPeople()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("People In Space") })
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            people.forEach { person ->
                PersonView(person, {})
            }
        }
    }
}


@Composable
fun PersonView(person: Assignment, personSelected: (person: Assignment) -> Unit) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { personSelected(person) })
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val personImageUrl = person.personImageUrl ?: ""
//        if (personImageUrl.isNotEmpty()) {
//            Image(
//                painter = rememberImagePainter(personImageUrl),
//                modifier = Modifier.size(60.dp), contentDescription = person.name
//            )
//        } else {
//            Spacer(modifier = Modifier.size(60.dp))
//        }
//
        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(text = person.name, style = TextStyle(fontSize = 20.sp))
            Text(text = person.craft, style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
        }
    }
}