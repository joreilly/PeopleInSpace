@file:OptIn(ExperimentalComposeLayoutApi::class)

package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import coil.ImageLoader
import com.google.android.horologist.compose.navscaffold.ExperimentalComposeLayoutApi
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import org.koin.android.ext.android.inject

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
