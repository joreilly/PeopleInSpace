package com.surrus.peopleinspace.glance

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PeopleInSpaceWidget: GlanceAppWidget(), KoinComponent {
    private val repository: PeopleInSpaceRepositoryInterface by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val people: List<Assignment> = withContext(Dispatchers.IO) {
            repository.fetchPeopleAsFlow().first()
        }

        provideContent {
            LazyColumn(
                modifier = GlanceModifier.background(Color.DarkGray).padding(horizontal = 8.dp)
            ) {
                item {
                    Text(
                        modifier = GlanceModifier.padding(bottom = 8.dp),
                        text = "People in Space",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = TextUnit(12f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                items(people.size) {
                    Row {
                        Text(
                            text = people[it].name,
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = TextUnit(10f, TextUnitType.Sp)
                            )
                        )
                    }
                }
            }
        }
    }
}