package com.surrus.peopleinspace.tile

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ProtoLayoutScope
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.material3.ButtonDefaults.filledTonalButtonColors
import androidx.wear.protolayout.material3.ButtonGroupDefaults.DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.Typography
import androidx.wear.protolayout.material3.buttonGroup
import androidx.wear.protolayout.material3.materialScopeWithResources
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textButton
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import com.surrus.peopleinspace.DEEPLINK_URI
import com.surrus.peopleinspace.tile.PeopleInSpaceTile.Data
import com.surrus.peopleinspace.tile.util.MultiRoundDevicesWithFontScalePreviews
import com.surrus.peopleinspace.tile.util.column
import com.surrus.peopleinspace.tile.util.isLargeScreen
import dev.johnoreilly.common.remote.Assignment

fun peopleList(
    context: Context,
    deviceParameters: DeviceParameters,
    data: Data,
    scope: ProtoLayoutScope
): LayoutElementBuilders.LayoutElement {
    return materialScopeWithResources(
        context = context,
        deviceConfiguration = deviceParameters,
        allowDynamicTheme = true,
        protoLayoutScope = scope
    ) {
        val visibleContacts = data.people.take(if (isLargeScreen()) 6 else 4)

        val (row1, row2) =
            visibleContacts.chunked(if (visibleContacts.size > 4) 3 else 2).let { chunkedList ->
                Pair(
                    chunkedList.getOrElse(0) { emptyList() },
                    chunkedList.getOrElse(1) { emptyList() }
                )
            }

        primaryLayout(
            titleSlot =
                // Only display the title if there's one row, otherwise the touch targets become
                // too small (less than 48dp). See
                // https://developer.android.com/training/wearables/accessibility#set-minimum
                if (row2.isEmpty()) {
                    { text(text = "People in Space".layoutString) }
                } else {
                    null
                },
            mainSlot = {
                column {
                    setWidth(expand())
                    setHeight(expand())
                    addContent(
                        buttonGroup {
                            row1.forEach {
                                buttonGroupItem {
                                    peopleButton(
                                        it,
                                        data.images,
                                        context
                                    )
                                }
                            }
                        }
                    )
                    if (!row2.isEmpty()) {
                        addContent(DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS)
                        addContent(
                            buttonGroup {
                                row2.forEach {
                                    buttonGroupItem {
                                        peopleButton(
                                            it,
                                            data.images,
                                            context
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            },
            bottomSlot = {
                val clickable = with(protoLayoutScope) {
                    val pendingIntent = homeIntent(context)
                    if (pendingIntent != null) {
                        clickable(id = "home", pendingIntent = pendingIntent)
                    } else {
                        clickable(id = "home")
                    }
                }
                textEdgeButton(
                    onClick = clickable,
                    labelContent = { text("Home".layoutString) },
                    colors = filledTonalButtonColors()
                )
            }
        )
    }
}

fun MaterialScope.peopleButton(
    person: Assignment,
    images: Map<Assignment, ImageResource?>,
    context: Context
): LayoutElementBuilders.LayoutElement {
    val clickable = with(protoLayoutScope) {
        val pendingIntent = personIntent(person, context)
        if (pendingIntent != null) {
            clickable(id = person.name, pendingIntent = pendingIntent)
        } else {
            clickable(id = person.name)
        }
    }
    return textButton(
        onClick = clickable,
        width = expand(),
        height = expand(),
        shape = shapes.full,
        labelContent = {
            text(
                person.name.layoutString,
                maxLines = 2,
                typography = Typography.BODY_SMALL
            )
        },
    )
}

@MultiRoundDevicesWithFontScalePreviews
internal fun namesPreview(context: Context): TilePreviewData {
    val contacts = Data(
        people = listOf(
            Assignment(
                "Apollo 11",
                "Neil Armstrong",
                "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cfl_progressive%2Cq_auto:good%2Cw_1200/MTc5OTk0MjgyMzk5MTE0MzYy/gettyimages-150832381.jpg"
            ),
            Assignment(
                "Apollo 11",
                "Buzz Aldrin",
                "https://nypost.com/wp-content/uploads/sites/2/2018/06/buzz-aldrin.jpg?quality=80&strip=all"
            )
        ), mapOf()
    )
    return TilePreviewData {
        TilePreviewHelper.singleTimelineEntryTileBuilder(
            peopleList(context, it.deviceConfiguration, contacts, it.scope)
        )
            .build()
    }
}

private fun personIntent(person: Assignment, context: Context): PendingIntent? {
    val sessionDetailIntent = Intent(
        Intent.ACTION_VIEW,
        (DEEPLINK_URI + "personList/{${person.name}}").toUri()
    )

    return PendingIntent.getActivity(
        context,
        0,
        sessionDetailIntent,
        FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
    )
}

private fun homeIntent(context: Context): PendingIntent? {
    val sessionDetailIntent = Intent(
        Intent.ACTION_VIEW,
        ("${DEEPLINK_URI}personList").toUri()
    )

    return PendingIntent.getActivity(
        context,
        0,
        sessionDetailIntent,
        FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
    )
}

@MultiRoundDevicesWithFontScalePreviews
internal fun twoRowsPreview(context: Context): TilePreviewData {
    val contacts = Data(
        people = listOf(
            Assignment(
                "Apollo 11",
                "Neil Armstrong",
                "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cfl_progressive%2Cq_auto:good%2Cw_1200/MTc5OTk0MjgyMzk5MTE0MzYy/gettyimages-150832381.jpg"
            ),
            Assignment(
                "Apollo 11",
                "Buzz Aldrin",
                "https://nypost.com/wp-content/uploads/sites/2/2018/06/buzz-aldrin.jpg?quality=80&strip=all"
            ),
            Assignment(
                "Vostok 1",
                "Yuri Gagarin",
                "https://nypost.com/wp-content/uploads/sites/2/2018/06/buzz-aldrin.jpg?quality=80&strip=all"
            ),
            Assignment(
                "Sputnik 2",
                "Laika",
                "https://nypost.com/wp-content/uploads/sites/2/2018/06/buzz-aldrin.jpg?quality=80&strip=all"
            )
        ), mapOf()
    )
    return TilePreviewData {
        TilePreviewHelper.singleTimelineEntryTileBuilder(
            peopleList(context, it.deviceConfiguration, contacts, it.scope)
        )
            .build()
    }
}