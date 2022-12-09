package com.surrus.peopleinspace.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.R
import com.surrus.peopleinspace.person.AstronautImage
import com.surrus.peopleinspace.util.ReportFullyDrawn
import com.surrus.peopleinspace.util.rememberStateWithLifecycle
import org.koin.androidx.compose.getViewModel

const val PersonListTag = "PersonList"
const val PersonTag = "Person"

@Composable
fun PersonListScreen(
    personSelected: (person: Assignment) -> Unit,
    issMapClick: () -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    val viewModel = getViewModel<PersonListViewModel>()
    val people by rememberStateWithLifecycle(viewModel.peopleInSpace)

    PersonList(
        people = people,
        personSelected = personSelected,
        issMapClick = issMapClick,
        columnState = columnState,
        modifier = modifier,
    )
}

@Composable
fun PersonList(
    people: List<Assignment>,
    personSelected: (person: Assignment) -> Unit,
    issMapClick: () -> Unit,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(
        modifier = modifier
            .testTag(PersonListTag),
        columnState = columnState,
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    modifier = Modifier
                        .size(ButtonDefaults.SmallButtonSize)
                        .wrapContentSize()
                    ,
                    onClick = issMapClick
                ) {
                    // https://www.svgrepo.com/svg/170716/international-space-station
                    Image(
                        modifier = Modifier.scale(0.5f),
                        painter = painterResource(id = R.drawable.ic_iss),
                        contentDescription = "ISS Map"
                    )
                }
            }
        }
        items(people.size) { offset ->
            PersonView(
                person = people[offset],
                personSelected = personSelected
            )

            // When the column has triggered drawing real
            // content - report fully drawn
            ReportFullyDrawn()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PersonView(
    modifier: Modifier = Modifier,
    person: Assignment,
    personSelected: (person: Assignment) -> Unit
) {
    Card(
        onClick = { personSelected(person) },
        modifier = modifier
            .testTag(PersonTag)
            .semantics(mergeDescendants = true) {
                contentDescription = person.name + " on " + person.craft
                testTagsAsResourceId = true
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AstronautImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(MaterialTheme.shapes.medium),
                person = person
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
    device = "id:wearos_small_round",
    showSystemUi = true
)
@Composable
fun PersonViewPreview() {
    PersonView(
        person = Assignment(
            "ISS",
            "Megan McArthur",
            personImageUrl = "https://www.nasa.gov/sites/default/files/styles/full_width_feature/public/thumbnails/image/jsc2021e010823.jpg"
        ),
        personSelected = {},
    )
}

@Preview(
    device = "id:wearos_small_round",
    showSystemUi = true
)
@Composable
fun PersonListSquarePreview() {
    PersonList(
        people = listOf(
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
        ),
        personSelected = {},
        issMapClick = {},
        columnState = ScalingLazyColumnDefaults.belowTimeText().create()
    )
}

@Preview(
    device = "id:wearos_small_round",
    showSystemUi = true
)
@Composable
fun PersonListSquareEmptyPreview() {
    PersonList(
        people = listOf(),
        personSelected = {},
        issMapClick = {},
        columnState = ScalingLazyColumnDefaults.belowTimeText().create()
    )
}
