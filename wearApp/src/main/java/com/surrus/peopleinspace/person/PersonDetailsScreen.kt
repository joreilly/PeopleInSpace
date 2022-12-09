package com.surrus.peopleinspace.person

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.R
import com.surrus.peopleinspace.list.PersonListTag
import com.surrus.peopleinspace.util.rememberStateWithLifecycle
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PersonDetailsScreen(
    personName: String,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    val peopleInSpaceViewModel = getViewModel<PersonDetailsViewModel>(
        parameters = { parametersOf(personName) }
    )
    val person by rememberStateWithLifecycle(peopleInSpaceViewModel.person)

    PersonDetails(
        modifier = modifier,
        person = person,
        columnState = columnState
    )
}

@Composable
private fun PersonDetails(
    person: Assignment?,
    columnState: ScalingLazyColumnState,
    modifier: Modifier = Modifier,
) {
    ScalingLazyColumn(
        modifier = modifier
            .testTag(PersonListTag),
        columnState = columnState
    ) {
        item {
            AstronautImage(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CutCornerShape(30.dp))
                ,
                person = person
            )
        }

        item {
            Text(
                person?.name ?: "Astronaut not found.",
                style = MaterialTheme.typography.title1,
                textAlign = TextAlign.Center
            )
        }

        val personBio = person?.personBio
        if (personBio != null) {
            item {
                Text(
                    personBio,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Justify
                )
            }
        }

        // Temp fix for https://issuetracker.google.com/issues/231701348
        item {
            Spacer(modifier = Modifier)
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

@Preview(
    device = "id:wearos_small_round",
    showSystemUi = true
)
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
            columnState = ScalingLazyColumnDefaults.belowTimeText().create(),
        )
    }
}

@Preview(
    device = "id:wearos_small_round",
    showSystemUi = true
)
@Composable
fun PersonDetailsScreenNotFoundPreview() {
    Box(modifier = Modifier.background(Color.Black)) {
        PersonDetails(
            person = null,
            columnState = ScalingLazyColumnDefaults.belowTimeText().create(),
        )
    }
}
