package com.surrus.peopleinspace.glance


import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.layout.LazyColumn
import androidx.glance.appwidget.layout.items
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Text
import androidx.glance.layout.fillMaxSize
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepository

class MyAppWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        val repo = remember { PeopleInSpaceRepository() }

        var people by remember { mutableStateOf(emptyList<Assignment>()) }

        LaunchedEffect(true) {
            people = repo.fetchPeople()
        }

        Text("hey there, count = " + people.size)


        val stops = listOf("Stop 1", "Stop 2", "Stop 3")
//        LazyColumn(
//            modifier = GlanceModifier.fillMaxSize()
//                .background(MaterialTheme.colors.background))
//        {
////            items(people) { person ->
////                Text(person.name)
////            }
//
//            items(stops) { stop ->
//                Text(stop)
//            }
//
//        }
    }
}