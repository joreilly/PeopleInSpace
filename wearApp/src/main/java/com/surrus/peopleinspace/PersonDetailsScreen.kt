package com.surrus.peopleinspace

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import coil.compose.AsyncImage
import com.surrus.common.remote.Assignment
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
    PersonDetailsScreen(person, scrollState)
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
private fun PersonDetailsScreen(
    person: Assignment?,
    scrollState: ScrollState = rememberScrollState(),
) {
    MaterialTheme {
        Scaffold(
            vignette = {
                if (person != null) {
                    Vignette(vignettePosition = VignettePosition.Bottom)
                }
            },
            positionIndicator = {
                if (person != null) {
                    PositionIndicator(scrollState = scrollState)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .rotaryEventHandler(scrollState)
                    .fillMaxSize()
                    .padding(horizontal = if (LocalConfiguration.current.isScreenRound) 18.dp else 8.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(if (LocalConfiguration.current.isScreenRound) 32.dp else 12.dp))

                AstronautImage(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CutCornerShape(30.dp)),
                    person = person
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    person?.name ?: "Astronaut not found.",
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center
                )

                val personBio = person?.personBio
                if (personBio != null) {
                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        personBio,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(modifier = Modifier.size(if (LocalConfiguration.current.isScreenRound) 48.dp else 12.dp))
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
        PersonDetailsScreen(person)
    }
}

@Preview(
    device = "id:wearos_small_round",
    showSystemUi = true
)
@Composable
fun PersonDetailsScreenNotFoundPreview() {
    Box(modifier = Modifier.background(Color.Black)) {
        PersonDetailsScreen(null)
    }
}
