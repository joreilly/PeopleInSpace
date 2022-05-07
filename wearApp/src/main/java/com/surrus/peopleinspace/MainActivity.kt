package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController

sealed class Screen(val route: String) {
    object PersonList : Screen("personList")
    object PersonDetails : Screen("personDetails")
    object IssMap : Screen("issMap")
}

const val PERSON_NAME_NAV_ARGUMENT = "personName"
const val DEEPLINK_URI = "peopleinspace://peopleinspace.dev/"

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            navController = rememberSwipeDismissableNavController()

            PeopleInSpaceApp(navController = navController)
        }
    }
}
