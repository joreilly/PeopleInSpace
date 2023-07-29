package com.surrus.peopleinspace.glance

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.peopleinspace.MainActivity
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
            Column(
                modifier = GlanceModifier.fillMaxSize().background(Color.Gray).padding(8.dp)
                .clickable(
                    actionStartActivity<MainActivity>()
                )
            ) {
                LazyColumn {
                    item {
                        Row(
                            GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            Text(
                                modifier = GlanceModifier.padding(16.dp),
                                text = "People in Space",
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontSize = TextUnit(16f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    items(people.size) {
                        Row(
                            GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                        ) {
                            Text(
                                text = people[it].name,
                                style = TextStyle(
                                    color = ColorProvider(Color.White),
                                    fontSize = TextUnit(14f, TextUnitType.Sp)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}