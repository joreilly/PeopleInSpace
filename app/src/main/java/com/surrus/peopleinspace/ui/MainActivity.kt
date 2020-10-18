package com.surrus.peopleinspace.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.compose.*
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.surrus.common.remote.Assignment
import dev.chrisbanes.accompanist.coil.CoilImage
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val peopleInSpaceViewModel: PeopleInSpaceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainLayout(peopleInSpaceViewModel)
        }
    }
}


sealed class Screen(val title: String) {
    object PersonListScreen : Screen("PersonList")
    object PersonDetailsDetails : Screen("PersonDetails")
}

@Composable
fun MainLayout(peopleInSpaceViewModel: PeopleInSpaceViewModel) {
    val navController = rememberNavController()

    PeopleInSpaceTheme {
        NavHost(navController, startDestination = Screen.PersonListScreen.title) {
            composable(Screen.PersonListScreen.title) {
                PersonList(peopleInSpaceViewModel = peopleInSpaceViewModel,
                    personSelected = {
                        navController.navigate(Screen.PersonDetailsDetails.title, bundleOf("person" to it.name))
                    }
                )
            }
            composable(Screen.PersonDetailsDetails.title) { backStackEntry ->
                PersonDetailsView(peopleInSpaceViewModel, backStackEntry.arguments?.get("person") as String)
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
fun PersonDetailsView(peopleInSpaceViewModel: PeopleInSpaceViewModel, personName: String) {
    val navController = AmbientNavController.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(personName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                }
            )
        },
        bodyContent = {
            ScrollableColumn(modifier = Modifier.padding(16.dp) + Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val person = peopleInSpaceViewModel.getPerson(personName)
                person?.let {
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