package com.surrus.peopleinspace

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import org.koin.androidx.compose.getViewModel

const val PersonListTag = "PersonList"
const val PersonTag = "Person"
const val NoPeopleTag = "NoPeople"

@Composable
fun PersonListScreen(
    personSelected: (person: Assignment) -> Unit,
    peopleInSpaceViewModel: PeopleInSpaceViewModel = getViewModel()
) {
    val peopleState by peopleInSpaceViewModel.peopleInSpace.collectAsState()

    PersonListScreen(peopleState, personSelected)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PersonListScreen(
    people: List<Assignment>?,
    personSelected: (person: Assignment) -> Unit
) {
    MaterialTheme {
        AnimatedVisibility(
            visible = people != null,
            enter = slideInVertically()
        ) {
            if (people != null) {
                if (people.isNotEmpty()) {
                    PersonList(people, personSelected)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            onClick = { },
                            modifier = Modifier.testTag(NoPeopleTag)
                        ) {
                            Text("No people in space!")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonList(
    people: List<Assignment>,
    personSelected: (person: Assignment) -> Unit
) {
    val paddingHeight = if (LocalConfiguration.current.isScreenRound) 50.dp else 8.dp
    ScalingLazyColumn(
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = paddingHeight,
            bottom = paddingHeight
        ),
        modifier = Modifier.testTag(PersonListTag)
    ) {
        items(people.size) { offset ->
            PersonView(people[offset], personSelected)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PersonView(person: Assignment, personSelected: (person: Assignment) -> Unit) {
    Card(
        onClick = { personSelected(person) },
        modifier = Modifier
            .testTag(PersonTag)
            .semantics(mergeDescendants = true) {
                contentDescription = person.name + " on " + person.craft
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberImagePainter(person.personImageUrl) {
                    // Use the generic astronaut SVG for missing or error (404?).
                    fallback(R.drawable.ic_american_astronaut)
                    error(R.drawable.ic_american_astronaut)

                    if (LocalInspectionMode.current) {
                        // Show error image instead of blank in @Preview
                        placeholder(R.drawable.ic_american_astronaut)
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentDescription = person.name
            )

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

@Preview(
    widthDp = 300,
    heightDp = 300,
    apiLevel = 26,
    uiMode = Configuration.UI_MODE_TYPE_WATCH,
    backgroundColor = 0x000000,
    showBackground = true
)
@Composable
fun PersonListSquarePreview() {
    PersonListScreen(people = listOf(
        Assignment(
            "Apollo 11",
            "Neil Armstrong",
            "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cfl_progressive%2Cq_auto:good%2Cw_1200/MTc5OTk0MjgyMzk5MTE0MzYy/gettyimages-150832381.jpg"
        ),
        Assignment(
            "Apollo 11",
            "Buzz Aldrin",
            "https://nypost.com/wp-content/uploads/sites/2/2018/06/buzz-aldrin.jpg?quality=80&strip=all"
        )
    ), personSelected = {})
}

@Preview(
    widthDp = 300,
    heightDp = 300,
    apiLevel = 26,
    uiMode = Configuration.UI_MODE_TYPE_WATCH,
    backgroundColor = 0x000000,
    showBackground = true
)
@Composable
fun PersonListSquareEmptyPreview() {
    PersonListScreen(people = listOf(), personSelected = {})
}
