package com.surrus.peopleinspace.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.compose.*
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.BuildConfig
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // needed for osmandroid
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        setContent {
            MainLayout()
        }
    }
}


sealed class Screen(val title: String) {
    object PersonList : Screen("PersonList")
    object PersonDetails : Screen("PersonDetails")
    object ISSPositionScreen : Screen("ISSPosition")
}

data class BottomNavigationitem(
    val route: String,
    val icon: ImageVector,
    val iconContentDescription: String
)

val bottomNavigationItems = listOf(
    BottomNavigationitem(
        Screen.PersonList.title,
        Icons.Default.Person,
        "People"
    ),
    BottomNavigationitem(
        Screen.ISSPositionScreen.title,
        Icons.Filled.LocationOn,
        "ISS Position"
    )
)

@Composable
fun MainLayout() {
    val navController = rememberNavController()

    PeopleInSpaceTheme {
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

                    bottomNavigationItems.forEach { bottomNavigationitem ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    bottomNavigationitem.icon,
                                    contentDescription = bottomNavigationitem.iconContentDescription
                                )
                            },
                            selected = currentRoute == bottomNavigationitem.route,
                            onClick = {
                                navController.navigate(bottomNavigationitem.route) {
                                    popUpTo = navController.graph.startDestination
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(navController, startDestination = Screen.PersonList.title) {
                composable(Screen.PersonList.title) {
                    PersonListScreen(paddingValues = paddingValues,
                        personSelected = {
                            navController.navigate(Screen.PersonDetails.title + "/${it.name}")
                        }
                    )
                }
                composable(Screen.PersonDetails.title + "/{person}") { backStackEntry ->
                    PersonDetailsScreen(
                        backStackEntry.arguments?.get("person") as String,
                        popBack = { navController.popBackStack() })
                }
                composable(Screen.ISSPositionScreen.title) {
                    ISSPositionScreen()
                }
            }
        }
    }
}


@Preview
@Composable
fun DefaultPreview(@PreviewParameter(PersonProvider::class) person: Assignment) {
    MaterialTheme {
        PersonView("", person, personSelected = {})
    }
}