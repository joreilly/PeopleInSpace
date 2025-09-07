package dev.johnoreilly.peopleinspace.ui


import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.peopleinspace.R
import dev.johnoreilly.peopleinspace.issposition.ISSPositionRoute
import dev.johnoreilly.peopleinspace.persondetails.PersonDetailsScreen
import dev.johnoreilly.peopleinspace.personlist.PersonListRoute
import kotlinx.coroutines.launch

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    PERSON_LIST(R.string.people, Icons.Default.Person, R.string.people),
    ISS_POSITION(R.string.iss_position, Icons.Default.LocationOn, R.string.iss_position),
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PeopleInSpaceApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.PERSON_LIST) }
    val navigator = rememberListDetailPaneScaffoldNavigator<Assignment>()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    PeopleInSpaceTheme {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = stringResource(it.contentDescription)
                            )
                        },
                        label = { Text(stringResource(it.label)) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            when (currentDestination) {
                AppDestinations.PERSON_LIST -> {
                    ListDetailPaneScaffold(
                        directive = navigator.scaffoldDirective,
                        value = navigator.scaffoldValue,
                        listPane = {
                            PersonListRoute { person ->
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, person)
                                }
                            }
                        },
                        detailPane = {
                            navigator.currentDestination?.contentKey?.let {
                                PersonDetailsScreen(
                                    person = it,
                                    showBackButton = !navigator.isListPaneVisible(),
                                    popBack = {
                                        scope.launch { navigator.navigateBack() }
                                    }
                                )
                            }
                        }
                    )
                }
                AppDestinations.ISS_POSITION -> {
                    ISSPositionRoute()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded


