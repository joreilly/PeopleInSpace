package com.surrus.peopleinspace.tile

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.peopleinspace.tile.util.BaseGlanceTileService
import kotlinx.coroutines.flow.first
import org.koin.core.component.inject

class PeopleInSpaceTile : BaseGlanceTileService<PeopleInSpaceTile.Data>() {
    val repository: PeopleInSpaceRepositoryInterface by inject()

    data class Data(val people: List<Assignment>)

    override suspend fun loadData(): Data {
        return Data(repository.fetchPeopleAsFlow().first())
    }

    @OptIn(ExperimentalUnitApi::class)
    @Composable
    override fun Content(data: Data) {
        Column(
            modifier = GlanceModifier.padding(horizontal = 8.dp)
        ) {
            Text(
                modifier = GlanceModifier.padding(bottom = 8.dp),
                text = "People in Space",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
            )
            data.people.forEach {
                Text(
                    text = it.name,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = TextUnit(10f, TextUnitType.Sp)
                    )
                )
            }
        }
    }
}