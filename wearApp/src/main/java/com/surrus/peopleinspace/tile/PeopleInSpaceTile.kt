@file:OptIn(ExperimentalHorologistApi::class)

package com.surrus.peopleinspace.tile

import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.android.horologist.tiles.images.toImageResource
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PeopleInSpaceTile : SuspendingTileService(), KoinComponent {
    val repository: PeopleInSpaceRepositoryInterface by inject()
    val imageLoader: ImageLoader by inject()

    data class Data(val people: List<Assignment>, val images: Map<Assignment, ImageResource?>)

    suspend fun loadData(): Data {
        val people = repository.fetchPeopleAsFlow().first()
        val images = fetchImages(people)
        return Data(people, images)
    }

    private suspend fun fetchImages(people: List<Assignment>): Map<Assignment, ImageResource?> {
        return withContext(Dispatchers.Default) {
            people.map { async { it to fetchImage(it) } }
        }.awaitAll().toMap()
    }

    private suspend fun fetchImage(person: Assignment): ImageResource? {
        val imageResult = imageLoader.execute(ImageRequest.Builder(this).size(96).build())
        return imageResult.image?.toBitmap()?.toImageResource()
    }

    override suspend fun tileRequest(requestParams: TileRequest): Tile {
        val data = loadData()

        val layoutElement =
            peopleList(this, requestParams.deviceConfiguration, data, requestParams.scope)

        return Tile.Builder()
            .setTileTimeline(Timeline.fromLayoutElement(layoutElement))
            .build()
    }

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): Resources {
        return Resources.Builder().build()
    }
}