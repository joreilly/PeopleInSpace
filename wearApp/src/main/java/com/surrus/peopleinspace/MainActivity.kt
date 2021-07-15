package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import coil.compose.rememberImagePainter
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepository
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val peopleInSpaceRepository: PeopleInSpaceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PersonList(peopleInSpaceRepository) {

                }
            }
        }

    }
}

@Composable
fun PersonList(peopleInSpaceRepository: PeopleInSpaceRepository, personSelected: (person: Assignment) -> Unit) {
    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }

    LaunchedEffect(true) {
        peopleState = peopleInSpaceRepository.fetchPeople()
    }

    Column {
        Text("PeopleInSpace", style = MaterialTheme.typography.title1)
        Spacer(modifier = Modifier.size(12.dp))

        LazyColumn {
            items(peopleState) { person ->
                PersonView(person, personSelected)
            }
        }
    }
}


@Composable
fun PersonView(person: Assignment, personSelected: (person: Assignment) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { personSelected(person) })
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val personImageUrl = person.personImageUrl ?: ""
        if (personImageUrl.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(personImageUrl),
                modifier = Modifier.size(50.dp), contentDescription = person.name
            )
        } else {
            Spacer(modifier = Modifier.size(50.dp))
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(text = person.name, style = TextStyle(fontSize = 15.sp))
            Text(text = person.craft, style = TextStyle(color = Color.Gray, fontSize = 13.sp))
        }
    }
}
