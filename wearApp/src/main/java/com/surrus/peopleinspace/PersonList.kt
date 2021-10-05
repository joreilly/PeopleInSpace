package com.surrus.peopleinspace

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface

@Composable
fun PersonList(
    peopleInSpaceRepository: PeopleInSpaceRepositoryInterface,
    personSelected: (person: Assignment) -> Unit
) {
    var peopleState by remember { mutableStateOf(emptyList<Assignment>()) }

    LaunchedEffect(true) {
        peopleState = peopleInSpaceRepository.fetchPeople()
    }

    ScalingLazyColumn(
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 50.dp, bottom = 50.dp)
    ) {
        items(peopleState.size) { offset ->
            PersonView(peopleState[offset], personSelected)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PersonView(person: Assignment, personSelected: (person: Assignment) -> Unit) {
    Card(onClick = { personSelected(person) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val personImageUrl = person.personImageUrl ?: ""
            if (personImageUrl.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(personImageUrl),
                    modifier = Modifier.size(50.dp),
                    contentDescription = person.name
                )
            } else {
                Spacer(modifier = Modifier.size(50.dp))
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(text = person.name, maxLines = 2, style = MaterialTheme.typography.body1)
                Text(
                    text = person.craft,
                    maxLines = 1,
                    style = MaterialTheme.typography.body2.copy(color = Color.Gray)
                )
            }
        }
    }
}

@Preview(
    widthDp = 300,
    heightDp = 80,
    apiLevel = 26,
    uiMode = Configuration.UI_MODE_TYPE_WATCH,
    backgroundColor = 0x000000,
    showBackground = true
)
@Composable
fun PersonViewPreview() {
    PersonView(
        person = Assignment(
            "ISS",
            "Megan McArthur",
            personImageUrl = "https://www.nasa.gov/sites/default/files/styles/full_width_feature/public/thumbnails/image/jsc2021e010823.jpg"
        ),
        personSelected = {})
}