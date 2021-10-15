package com.surrus.peopleinspace

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.samples.shared.fillMaxRectangle
import coil.compose.rememberImagePainter
import org.koin.androidx.compose.getViewModel

@Composable
fun PersonDetailsScreen(personName: String) {
    val peopleInSpaceViewModel = getViewModel<PeopleInSpaceViewModel>()

    // TODO look for cleaner way of doing this
    val peopleState = peopleInSpaceViewModel.peopleInSpace.collectAsState()

    val person by remember(peopleState, personName) {
        derivedStateOf {
            peopleState.value.find { it.name == personName }
        }
    }

    val scrollState = rememberScrollState()

    person?.let { person ->

        Box(modifier = Modifier
            .fillMaxSize()
            .fillMaxRectangle()) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    person.name,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(12.dp))

                val imageUrl = person.personImageUrl ?: ""
                if (imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(imageUrl),
                        modifier = Modifier.size(120.dp), contentDescription = person.name
                    )
                }
                Spacer(modifier = Modifier.size(24.dp))

                val bio = person.personBio ?: ""
                Text(bio, style = MaterialTheme.typography.body1, textAlign = TextAlign.Center)
            }
        }
    }
}
