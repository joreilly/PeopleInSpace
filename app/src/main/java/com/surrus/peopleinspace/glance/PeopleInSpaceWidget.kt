package com.surrus.peopleinspace.glance

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.background
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.peopleinspace.glance.util.BaseGlanceAppWidget
import kotlinx.coroutines.flow.first
import org.koin.core.component.inject

class PeopleInSpaceWidget : BaseGlanceAppWidget<PeopleInSpaceWidget.Data>() {
    val repository: PeopleInSpaceRepositoryInterface by inject()

    data class Data(val people: List<Assignment>)

    override suspend fun loadData(): Data {
        return Data(repository.fetchPeopleAsFlow().first())
    }

    @OptIn(ExperimentalUnitApi::class)
    @Composable
    override fun Content(data: Data?) {
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
            if (data != null) {
                items(data.people.size) {
                    Row {
                        Text(
                            text = data.people[it].name,
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