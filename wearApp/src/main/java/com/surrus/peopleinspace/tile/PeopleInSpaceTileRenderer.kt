package com.surrus.peopleinspace.tile

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ActionBuilders.LaunchAction
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.ResourceBuilders.AndroidImageResourceByResId
import androidx.wear.tiles.ResourceBuilders.ImageResource
import androidx.wear.tiles.material.Button
import androidx.wear.tiles.material.CompactChip
import androidx.wear.tiles.material.Text
import androidx.wear.tiles.material.layouts.MultiButtonLayout
import androidx.wear.tiles.material.layouts.PrimaryLayout
import com.google.android.horologist.compose.tools.LayoutPreview
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.tiles.SingleTileLayoutRenderer
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.MainActivity
import com.surrus.peopleinspace.R
import com.surrus.peopleinspace.previews.AstronautSelections
import com.surrus.peopleinspace.previews.BuzzAldrin
import com.surrus.peopleinspace.previews.IconSizePreview
import com.surrus.peopleinspace.previews.NeilArmstrong
import com.surrus.peopleinspace.previews.WearPreviewDevices
import com.surrus.peopleinspace.previews.WearPreviewFontSizes
import com.surrus.peopleinspace.previews.WearSmallRoundDevicePreview
import com.surrus.peopleinspace.tile.PeopleInSpaceTileRenderer.Companion.idToImageResource
import org.koin.ext.getFullName
import kotlin.reflect.KClass

class PeopleInSpaceTileRenderer(context: Context) :
    SingleTileLayoutRenderer<PeopleInSpaceTile.Astronauts, PeopleInSpaceTile.Images>(context) {
    override fun renderTile(
        singleTileState: PeopleInSpaceTile.Astronauts,
        deviceParameters: DeviceParametersBuilders.DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        val astronautButtons = MultiButtonLayout.Builder()
            .apply {
                singleTileState.people.take(3).forEach {
                    addButtonContent(astronautButton(it))
                }
            }
            .build()

        val mapChip = mapChip(deviceParameters)

        return PrimaryLayout.Builder(deviceParameters)
            .setPrimaryLabelTextContent(titleText())
            .setContent(astronautButtons)
            .setPrimaryChipContent(mapChip)
            .build()
    }

    private fun titleText(): LayoutElementBuilders.LayoutElement {
        return Text.Builder(context, "Recent Astronauts")
            .setMaxLines(1)
            .build()
    }

    fun mapChip(deviceParameters: DeviceParametersBuilders.DeviceParameters) =
        CompactChip.Builder(context, "ISS", action(MainActivity::class), deviceParameters)
            .build()

    fun astronautButton(astronaut: Assignment): Button {
        return Button.Builder(context, action(MainActivity::class))
            .setImageContent(astronaut.name)
            .build()
    }

    fun action(activity: KClass<MainActivity>): Clickable {
        return Clickable.Builder()
            .setOnClick(
                LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setClassName(activity.getFullName())
                            .setPackageName(context.packageName)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    override fun ResourceBuilders.Resources.Builder.produceRequestedResources(
        resourceResults: PeopleInSpaceTile.Images,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: MutableList<String>
    ) {
        if (requested(resourceIds, "ic_iss")) {
            addIdToImageMapping(
                "ic_iss", idToImageResource(R.drawable.ic_iss)
            )
        }
        resourceResults.images.forEach { (id, image) ->
            if (image != null && requested(resourceIds, id)) {
                addIdToImageMapping(id, image)
            }
        }
    }

    companion object {
        fun idToImageResource(id: Int) = ImageResource.Builder()
            .setAndroidResourceByResId(
                AndroidImageResourceByResId.Builder()
                    .setResourceId(id)
                    .build()
            )
            .build()

        fun requested(
            resourceIds: MutableList<String>,
            id: String
        ): Boolean {
            return resourceIds.isEmpty() || resourceIds.contains(id)
        }
    }
}

@WearPreviewDevices
@WearPreviewFontSizes
@Composable
fun Astronauts() {
    val people = listOf(
        NeilArmstrong,
        BuzzAldrin,
        NeilArmstrong,
        BuzzAldrin,
        NeilArmstrong,
        BuzzAldrin
    )

    AstronautPreview(people)
}

@WearSmallRoundDevicePreview
@Composable
fun AstronautsByAmount(
    @PreviewParameter(AstronautSelections::class) astronauts: List<Assignment>
) {
    AstronautPreview(astronauts)
}

@Composable
fun AstronautPreview(people: List<Assignment>) {
    val context = LocalContext.current
    val renderer = remember { PeopleInSpaceTileRenderer(context) }

    val images = people.associate {
        Pair(it.name, idToImageResource(R.drawable.ic_american_astronaut))
    }

    TileLayoutPreview(
        state = PeopleInSpaceTile.Astronauts(people),
        resourceState = PeopleInSpaceTile.Images(images),
        renderer = renderer
    )
}

@IconSizePreview
@Composable
fun AstronautButtonPreview() {
    val context = LocalContext.current
    val renderer = remember { PeopleInSpaceTileRenderer(context) }

    LayoutPreview(
        renderer.astronautButton(NeilArmstrong)
    )
}