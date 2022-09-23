package com.surrus.peopleinspace.persondetails.navigation

import android.net.Uri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.surrus.peopleinspace.navigation.PeopleInSpaceNavigationDestination
import com.surrus.peopleinspace.persondetails.PersonDetailsRoute


object PersonDetailsDestination : PeopleInSpaceNavigationDestination {
    const val personArg = "person"
    override val route = "person_details_route/{$personArg}"
    override val destination = "person_details_destination"

    fun createNavigationRoute(personArg: String): String {
        val encodedId = Uri.encode(personArg)
        return "person_details_route/$encodedId"
    }

    fun fromNavArgs(entry: NavBackStackEntry): String {
        val encodedId = entry.arguments?.getString(personArg)!!
        return Uri.decode(encodedId)
    }
}


// can test using following (needs to be updated as astronauts change!)
// adb shell am start -d "peopleinspace://person/Samantha%20Cristoforetti" -a android.intent.action.VIEW
fun NavGraphBuilder.personDetailsGraph(onBackClick: () -> Unit) {
    composable(
        route = PersonDetailsDestination.route,
        arguments = listOf(
            navArgument(PersonDetailsDestination.personArg) { type = NavType.StringType }
        ),
        deepLinks = listOf(navDeepLink {
            uriPattern = "peopleinspace://person/{${PersonDetailsDestination.personArg}}"
        })
    ) {
        PersonDetailsRoute(onBackClick)
    }
}
