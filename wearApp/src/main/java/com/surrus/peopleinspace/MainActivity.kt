package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.surrus.common.di.initKoin
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.PeopleInSpaceApi

class MainActivity : ComponentActivity() {

    private val koin = initKoin(enableNetworkLogs = true).koin
    val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PersonList(peopleInSpaceApi)
            }
        }

    }
}

@Composable
fun PersonList(peopleInSpaceApi: PeopleInSpaceApi) {
    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }

    LaunchedEffect(true) {
        peopleState = peopleInSpaceApi.fetchPeople().people
    }

    Column {
        Text("PeopleInSpace", style = MaterialTheme.typography.title1)
        Spacer(modifier = Modifier.size(12.dp))

        LazyColumn {
            items(peopleState) { person ->
                Text("${person.name} (${person.craft})")
            }
        }
    }
}