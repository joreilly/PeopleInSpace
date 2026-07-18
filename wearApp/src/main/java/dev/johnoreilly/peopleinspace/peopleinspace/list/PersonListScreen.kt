package dev.johnoreilly.peopleinspace.peopleinspace.list

import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import dev.johnoreilly.peopleinspace.peopleinspace.person.AstronautImage
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.peopleinspace.R
import org.koin.androidx.compose.koinViewModel

const val PersonListTag = "PersonList"
const val PersonTag = "Person"

@Composable
fun PersonListScreen(
    personSelected: (person: Assignment) -> Unit,
    issMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<PersonListViewModel>()
    val people by viewModel.peopleInSpace.collectAsStateWithLifecycle()

    PersonList(
        people = people,
        personSelected = personSelected,
        issMapClick = issMapClick,
        modifier = modifier,
    )
}

@Composable
fun PersonList(
    people: List<Assignment>,
    personSelected: (person: Assignment) -> Unit,
    issMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding =
        rememberResponsiveColumnPadding(first = ItemType.Text, last = ItemType.Card)
    ScreenScaffold(
        scrollState = columnState,
        contentPadding = contentPadding,
        edgeButton = {
            EdgeButton(onClick = issMapClick) {
                // https://www.svgrepo.com/svg/170716/international-space-station
                Icon(
                    modifier = Modifier.scale(0.75f),
                    painter = painterResource(id = R.drawable.ic_iss),
                    contentDescription = "ISS Map"
                )
            }
        }
    ) { contentPadding ->
        val transformationSpec = rememberTransformationSpec()
        TransformingLazyColumn(
            modifier = modifier
                .testTag(PersonListTag),
            contentPadding = contentPadding,
            state = columnState,
        ) {
            item {
                ListHeader(
                    modifier = Modifier.transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text("People in Space")
                }
            }
            items(people.size) { offset ->
                PersonView(
                    modifier = Modifier.transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                    person = people[offset],
                    personSelected = personSelected
                )

                // When the column has triggered drawing real
                // content - report fully drawn
                ReportDrawn()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PersonView(
    modifier: Modifier = Modifier,
    person: Assignment,
    personSelected: (person: Assignment) -> Unit,
    transformation: SurfaceTransformation? = null,
) {
    Card(
        onClick = { personSelected(person) },
        transformation = transformation,
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
                    .clip(CircleShape),
                person = person
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(text = person.name, maxLines = 2, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = person.craft,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun PersonListSquarePreview() {
    AppScaffold {
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
        )
    }
}
