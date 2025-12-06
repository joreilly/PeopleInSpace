package dev.johnoreilly.peopleinspace.peopleinspace.person

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import coil3.compose.AsyncImage
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import dev.johnoreilly.peopleinspace.peopleinspace.list.PersonListTag
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.peopleinspace.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PersonDetailsScreen(
    personName: String,
    modifier: Modifier = Modifier,
) {
    val peopleInSpaceViewModel = koinViewModel<PersonDetailsViewModel>(
        parameters = { parametersOf(personName) }
    )
    val person by peopleInSpaceViewModel.person.collectAsStateWithLifecycle()

    PersonDetails(
        modifier = modifier,
        person = person,
    )
}

@Composable
private fun PersonDetails(
    person: Assignment?,
    modifier: Modifier = Modifier,
) {
    val columnState = rememberTransformingLazyColumnState()
    val contentPadding =
        rememberResponsiveColumnPadding(first = ItemType.Icon, last = ItemType.BodyText)
    ScreenScaffold(scrollState = columnState, contentPadding = contentPadding) { contentPadding ->
        TransformingLazyColumn(
            modifier = modifier
                .testTag(PersonListTag),
            contentPadding = contentPadding,
            state = columnState
        ) {
            item {
                AstronautImage(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CutCornerShape(30.dp)),
                    person = person
                )
            }

            item {
                Text(
                    person?.name ?: "Astronaut not found.",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }

            val personBio = person?.personBio
            if (personBio != null) {
                item {
                    Text(
                        personBio,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}

@Composable
fun AstronautImage(
    modifier: Modifier,
    person: Assignment?
) {
    AsyncImage(
        modifier = modifier,
        model = person?.personImageUrl,
        contentDescription = person?.name,
        fallback = painterResource(id = R.drawable.ic_american_astronaut),
        error = painterResource(id = R.drawable.ic_american_astronaut)
    )
}

@WearPreviewDevices
@Composable
fun PersonDetailsScreenPreview() {
    val person = remember {
        Assignment(
            "Apollo 11",
            "Neil Armstrong",
            "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cfl_progressive%2Cq_auto:good%2Cw_1200/MTc5OTk0MjgyMzk5MTE0MzYy/gettyimages-150832381.jpg",
            "Mark Thomas Vande Hei (born November 10, 1966) is a retired United States Army officer and NASA astronaut who served as a flight Engineer for Expedition 53 and 54 on the International Space Station."
        )
    }
    Box(modifier = Modifier.background(Color.Black)) {
        PersonDetails(
            person = person,
        )
    }
}

@WearPreviewDevices
@Composable
fun PersonDetailsScreenNotFoundPreview() {
    Box(modifier = Modifier.background(Color.Black)) {
        PersonDetails(
            person = null,
        )
    }
}
