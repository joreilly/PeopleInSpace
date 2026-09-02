package dev.johnoreilly.peopleinspace.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.viewmodel.PersonListUiState
import dev.johnoreilly.peopleinspace.persondetails.PersonBio
import dev.johnoreilly.peopleinspace.persondetails.PersonDetailsTopAppBar
import dev.johnoreilly.peopleinspace.persondetails.PersonImage
import dev.johnoreilly.peopleinspace.personlist.PersonListScreen
import dev.johnoreilly.peopleinspace.personlist.PersonView

// Previews for the screens and rows the app is built from.
//
// PersonListScreen already takes a PersonListUiState plus two lambdas rather than reaching for a
// ViewModel, so all three of its states can be rendered from literal data. That is the useful part:
// Loading and Error are the states hardest to reach by running the app, and the easiest to break.
//
// No preview here performs I/O. PersonImage is given a null URL throughout, which is the branch
// that draws the placeholder rather than the one that fetches over the network — so these render
// identically offline, in the IDE, and in a headless renderer.

private val SampleCrew = listOf(
    Assignment(
        craft = "ISS",
        name = "Oleg Kononenko",
        personImageUrl = null,
        personBio = "Russian cosmonaut and commander of Expedition 70, and the first person to " +
            "accumulate more than 1,000 days in space across five flights.",
        nationality = "Russia",
    ),
    Assignment(
        craft = "ISS",
        name = "Jasmin Moghbeli",
        personImageUrl = null,
        personBio = "NASA astronaut and test pilot, selected in 2017 and flight engineer for " +
            "Expedition 70.",
        nationality = "USA",
    ),
    Assignment(
        craft = "Tiangong",
        name = "Tang Shengjie",
        personImageUrl = null,
        personBio = null,
        nationality = "China",
    ),
)

// ---------------------------------------------------------------------------
// Stateless screens — every state of the list
// ---------------------------------------------------------------------------

@Preview
@Composable
fun PersonListScreenSuccessPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        PersonListScreen(
            uiState = PersonListUiState.Success(SampleCrew),
            navigateToPerson = {},
            onRefresh = {},
        )
    }
}

@Preview
@Composable
fun PersonListScreenLoadingPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        PersonListScreen(
            uiState = PersonListUiState.Loading,
            navigateToPerson = {},
            onRefresh = {},
        )
    }
}

@Preview
@Composable
fun PersonListScreenErrorPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        PersonListScreen(
            uiState = PersonListUiState.Error("Could not reach the Open Notify API."),
            navigateToPerson = {},
            onRefresh = {},
        )
    }
}

@Preview
@Composable
fun PersonListScreenEmptyPreview() {
    // Nobody in space is not a state the API produces often, but the list should still be sane.
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        PersonListScreen(
            uiState = PersonListUiState.Success(emptyList()),
            navigateToPerson = {},
            onRefresh = {},
        )
    }
}

@Preview
@Composable
fun PersonListScreenDarkPreview() {
    PeopleInSpaceTheme(darkTheme = true, disableDynamicTheming = true) {
        PersonListScreen(
            uiState = PersonListUiState.Success(SampleCrew),
            navigateToPerson = {},
            onRefresh = {},
        )
    }
}

// ---------------------------------------------------------------------------
// Key components — used by the list and the detail screen
// ---------------------------------------------------------------------------

@Preview
@Composable
fun PersonViewRowsPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        Surface {
            Column {
                SampleCrew.forEachIndexed { index, person ->
                    PersonView(index = index, person = person, personSelected = {})
                }
            }
        }
    }
}

@Preview
@Composable
fun PersonViewNoNationalityPreview() {
    // `nationality` is blank until the bio lookup completes; the row hides the line rather than
    // leaving a gap, which is worth being able to see on its own.
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        Surface {
            PersonView(
                index = 0,
                person = Assignment(craft = "ISS", name = "Unknown Crew Member", nationality = ""),
                personSelected = {},
            )
        }
    }
}

@Preview
@Composable
fun PersonDetailsTopAppBarPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        PersonDetailsTopAppBar(personName = "Oleg Kononenko", showBackButton = true, popBack = {})
    }
}

@Preview
@Composable
fun PersonDetailsTopAppBarNoBackPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        PersonDetailsTopAppBar(personName = "Jasmin Moghbeli", showBackButton = false, popBack = {})
    }
}

@Preview
@Composable
fun PersonImagePlaceholderPreview() {
    // Null URL: the placeholder branch, and the one a preview can render without the network.
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        Surface {
            PersonImage(imageUrl = null, name = "Oleg Kononenko")
        }
    }
}

@Preview
@Composable
fun PersonBioPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        Surface {
            PersonBio(bio = SampleCrew[0].personBio)
        }
    }
}

@Preview
@Composable
fun PersonBioMissingPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        Surface {
            PersonBio(bio = null)
        }
    }
}

// ---------------------------------------------------------------------------
// Theme catalog
// ---------------------------------------------------------------------------

@Composable
private fun Swatch(name: String, color: Color, onColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(color = color, shape = RoundedCornerShape(6.dp), modifier = Modifier.size(40.dp)) {
            Text(
                text = "Aa",
                color = onColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(8.dp),
            )
        }
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ColorSchemeSpecimen() {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Colour scheme", style = MaterialTheme.typography.titleMedium)
        Swatch("primary", scheme.primary, scheme.onPrimary)
        Swatch("primaryContainer", scheme.primaryContainer, scheme.onPrimaryContainer)
        Swatch("secondary", scheme.secondary, scheme.onSecondary)
        Swatch("secondaryContainer", scheme.secondaryContainer, scheme.onSecondaryContainer)
        Swatch("tertiary", scheme.tertiary, scheme.onTertiary)
        Swatch("surface", scheme.surface, scheme.onSurface)
        Swatch("surfaceVariant", scheme.surfaceVariant, scheme.onSurfaceVariant)
        Swatch("error", scheme.error, scheme.onError)
    }
}

@Preview
@Composable
fun ColorSchemeLightPreview() {
    // Dynamic theming disabled so the specimen shows the app's own palette rather than whatever
    // wallpaper the device happens to have.
    PeopleInSpaceTheme(darkTheme = false, disableDynamicTheming = true) {
        Surface { ColorSchemeSpecimen() }
    }
}

@Preview
@Composable
fun ColorSchemeDarkPreview() {
    PeopleInSpaceTheme(darkTheme = true, disableDynamicTheming = true) {
        Surface { ColorSchemeSpecimen() }
    }
}

@Preview
@Composable
fun ColorSchemeAndroidPreview() {
    // The `androidTheme = true` branch, which swaps in the Android-flavoured scheme.
    PeopleInSpaceTheme(darkTheme = false, androidTheme = true, disableDynamicTheming = true) {
        Surface { ColorSchemeSpecimen() }
    }
}

@Composable
private fun TypeSample(name: String, style: TextStyle) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(name, style = MaterialTheme.typography.labelSmall)
        Text("People In Space", style = style)
    }
}

@Preview
@Composable
fun TypographySpecimenPreview() {
    PeopleInSpaceTheme(disableDynamicTheming = true) {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                val type = MaterialTheme.typography
                TypeSample("headlineMedium", type.headlineMedium)
                TypeSample("titleLarge", type.titleLarge)
                TypeSample("titleMedium", type.titleMedium)
                TypeSample("bodyLarge", type.bodyLarge)
                TypeSample("bodyMedium", type.bodyMedium)
                TypeSample("labelLarge", type.labelLarge)
            }
        }
    }
}
