package com.surrus.peopleinspace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.issposition.ISSPositionRoute
import com.surrus.peopleinspace.persondetails.PersonDetailsScreen
import com.surrus.peopleinspace.personlist.PersonListRoute
import kotlinx.serialization.Serializable


@Serializable
object PersonList

@Serializable
object IssPosition


@Composable
fun PeopleInSpaceNavHost(
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = PersonList,
        modifier = modifier,
    ) {
        composable<PersonList> {
            PersonListRoute { person ->
                navController.navigate(person)
            }
        }
        composable<Assignment> { backStackEntry ->
            val person: Assignment = backStackEntry.toRoute()
            PersonDetailsScreen(person, onBackClick)
        }
        composable<IssPosition> {
            ISSPositionRoute()
        }
    }
}
