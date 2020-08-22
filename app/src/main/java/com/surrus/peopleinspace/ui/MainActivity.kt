package com.surrus.peopleinspace.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnItems
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import com.surrus.common.remote.Assignment
import dev.chrisbanes.accompanist.coil.CoilImage
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val peopleInSpaceViewModel: PeopleInSpaceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val peopleState = peopleInSpaceViewModel.peopleInSpace.observeAsState(emptyList())
            mainLayout(peopleState)
        }
    }
}

@Composable
fun mainLayout(peopleState: State<List<Assignment>>) {
    MaterialTheme {
        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("People In Space")
                        }
                    )
                },
                bodyContent = {
                    when (val screen = PeopleInSpaceNavigation.currentScreen) {
                        is Screen.Home -> PersonList(peopleState)
                        is Screen.PersonDetails -> PersonDetailsView(screen.person)
                    }
                }
            )
        }
    }
}

@Composable
fun PersonList(peopleState: State<List<Assignment>>) {
    LazyColumnItems(items = peopleState.value) { person ->
        PersonView(person, itemClick = { navigateTo(Screen.PersonDetails(it)) })
    }
}

val personImages = mapOf(
    "Chris Cassidy" to "https://www.nasa.gov/sites/default/files/styles/side_image/public/thumbnails/image/9368855148_f79942efb7_o.jpg?itok=-w5yoryN",
    "Anatoly Ivanishin" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Anatoli_Ivanishin_2011.jpg/440px-Anatoli_Ivanishin_2011.jpg",
    "Ivan Vagner" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Ivan_Vagner_%28Jsc2020e014992%29.jpg/440px-Ivan_Vagner_%28Jsc2020e014992%29.jpg",
    "Doug Hurley" to "https://www.nasa.gov/sites/default/files/thumbnails/image/9391325359_bb7f05ec52_o.jpg",
    "Bob Behnken" to "https://www.nasa.gov/sites/default/files/styles/side_image/public/thumbnails/image/9371018002_626f81d665_o.jpg?itok=0y23W3T5"
)

@Composable
fun PersonView(person: Assignment, itemClick : (person : Assignment) -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp) + Modifier.fillMaxWidth()
                + Modifier.clickable(onClick = { itemClick(person) }),
        verticalGravity = Alignment.CenterVertically
    ) {

        personImages[person.name]?.let { imageUrl ->
            CoilImage(
                data = imageUrl,
                modifier = Modifier.preferredSize(60.dp)
            )
        } ?: Spacer(modifier = Modifier.preferredSize(60.dp))


        Spacer(modifier = Modifier.preferredSize(12.dp))

        Column {
            Text(text = person.name, style = TextStyle(fontSize = 20.sp))
            Text(
                text = person.craft,
                style = TextStyle(color = Color.DarkGray, fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun PersonDetailsView(person: Assignment) {
    Text(person.name, style = TextStyle(fontSize = 20.sp))
}

class PersonProvider : CollectionPreviewParameterProvider<Assignment>(
    listOf(
        Assignment("ISS", "Chris Cassidy"),
        Assignment("ISS", "Anatoli Ivanishin")
    )
)

@Preview
@Composable
fun DefaultPreview(@PreviewParameter(PersonProvider::class) person: Assignment) {
    MaterialTheme {
        PersonView(person, itemClick = {})
    }
}