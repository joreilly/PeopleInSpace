package com.surrus.peopleinspace.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.BackStack
import com.github.zsoltk.compose.router.Router
import com.surrus.common.remote.Assignment
import dev.chrisbanes.accompanist.coil.CoilImage
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val backPressHandler = BackPressHandler()
    private val peopleInSpaceViewModel: PeopleInSpaceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Providers(AmbientBackPressHandler provides backPressHandler) {
                mainLayout(peopleInSpaceViewModel, Routing.PeopleList)
            }
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }
}


sealed class Routing {
    object PeopleList : Routing()
    data class PersonDetails(val person: Assignment) : Routing()
}

@Composable
fun mainLayout(peopleInSpaceViewModel: PeopleInSpaceViewModel, defaultRouting: Routing) {

    PeopleInSpaceTheme {
        Router(defaultRouting) { backStack ->
            when (val routing = backStack.last()) {
                is Routing.PeopleList -> PersonList(
                    peopleInSpaceViewModel = peopleInSpaceViewModel,
                    personSelected = {
                        backStack.push(Routing.PersonDetails(it))
                    }
                )
                is Routing.PersonDetails -> PersonDetailsView(peopleInSpaceViewModel, routing.person, backStack)
            }
        }
    }
}

@Composable
fun PersonList(peopleInSpaceViewModel: PeopleInSpaceViewModel, personSelected : (person : Assignment) -> Unit) {
    val peopleState = peopleInSpaceViewModel.peopleInSpace.collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("People In Space") })
        },
        bodyContent = {
            LazyColumnFor(items = peopleState.value, itemContent = { person ->
                val personImageUrl = peopleInSpaceViewModel.getPersonImage(person.name)
                PersonView(personImageUrl, person, personSelected)
            })
        }
    )
}

@Composable
fun PersonView(personImageUrl: String, person: Assignment, personSelected : (person : Assignment) -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp) + Modifier.fillMaxWidth()
                + Modifier.clickable(onClick = { personSelected(person) }),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (personImageUrl.isNotEmpty()) {
            CoilImage(data = personImageUrl, modifier = Modifier.preferredSize(60.dp))
        } else {
            Spacer(modifier = Modifier.preferredSize(60.dp))
        }

        Spacer(modifier = Modifier.preferredSize(12.dp))

        Column {
            Text(text = person.name, style = TextStyle(fontSize = 20.sp))
            Text(text = person.craft, style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
        }
    }
}

@Composable
fun PersonDetailsView(peopleInSpaceViewModel: PeopleInSpaceViewModel, person: Assignment, backStack: BackStack<Routing>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(person.name) },
                navigationIcon = {
                    IconButton(onClick = { backStack.pop() }) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                }
            )
        },
        bodyContent = {
            Column(modifier = Modifier.padding(16.dp) + Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(person.name, style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.preferredSize(12.dp))

                val imageUrl = peopleInSpaceViewModel.getPersonImage(person.name)
                if (imageUrl.isNotEmpty()) {
                    CoilImage(data = imageUrl, modifier = Modifier.preferredSize(240.dp))
                }
                Spacer(modifier = Modifier.preferredSize(24.dp))

                val bio = peopleInSpaceViewModel.getPersonBio(person.name)
                Text(bio, style = MaterialTheme.typography.body1)
            }
        }
    )
}


@Preview
@Composable
fun DefaultPreview(@PreviewParameter(PersonProvider::class) person: Assignment) {
    MaterialTheme {
        PersonView("", person, personSelected = {})
    }
}