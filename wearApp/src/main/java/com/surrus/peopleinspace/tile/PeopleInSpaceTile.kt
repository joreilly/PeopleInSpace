package com.surrus.peopleinspace.tile

import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.TileBuilders.Tile
import coil.ImageLoader
import com.google.android.horologist.tiles.CoroutinesTileService
import com.google.android.horologist.tiles.loadImageResource
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.peopleinspace.R
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.ext.android.inject
import kotlin.time.Duration.Companion.seconds

class PeopleInSpaceTile : CoroutinesTileService() {
    val repository: PeopleInSpaceRepositoryInterface by inject()
    val renderer: PeopleInSpaceTileRenderer by inject()
    val imageLoader: ImageLoader by inject()

    data class Astronauts(val people: List<Assignment>)
    data class Images(val images: Map<String, ImageResource?>)

    suspend fun loadData(): Astronauts {
        val people = repository.fetchPeopleAsFlow().first()
        return Astronauts(people)
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        val people = repository.fetchPeopleAsFlow().first()
        val resources = fetchImages(people)
        return renderer.produceRequestedResources(resources, requestParams)
    }

    private suspend fun fetchImages(people: List<Assignment>): Images {
        return coroutineScope {
            Images(people.map {
                async {
                    it.name to imageResource(it)
                }
            }.awaitAll().toMap())
        }
    }

    suspend fun imageResource(it: Assignment): ImageResource? =
        withTimeoutOrNull(2.seconds) {
            imageLoader.loadImageResource(
                this@PeopleInSpaceTile,
                it.personImageUrl
            ) {
                error(R.drawable.ic_american_astronaut)
                size(64)
            }
        }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): Tile {
        val tileState = loadData()
        return renderer.renderTimeline(tileState, requestParams)
    }
}