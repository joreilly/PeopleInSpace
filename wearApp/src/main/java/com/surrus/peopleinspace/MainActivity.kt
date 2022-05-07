package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.surrus.peopleinspace.util.JankPrinter

sealed class Screen(val route: String) {
    object PersonList : Screen("personList")
    object PersonDetails : Screen("personDetails")
    object IssMap : Screen("issMap")
}

const val PERSON_NAME_NAV_ARGUMENT = "personName"
const val DEEPLINK_URI = "peopleinspace://peopleinspace.dev/"

class MainActivity : ComponentActivity() {
    private lateinit var jankPrinter: JankPrinter
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        jankPrinter = JankPrinter()

        setContent {
            navController = rememberSwipeDismissableNavController()

            PeopleInSpaceApp(navController = navController)

            LaunchedEffect(Unit) {
                navController.currentBackStackEntryFlow.collect {
                    jankPrinter.setRouteState(route = it.destination.route)
                }
            }
        }

        jankPrinter.installJankStats(activity = this)
    }
}
