/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.johnoreilly.peopleinspace.peopleinspace.tile.util

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.tiles.RequestBuilders
import java.nio.ByteBuffer

// Resources extensions

fun resources(
    fn: Resources.Builder.() -> Unit
): (RequestBuilders.ResourcesRequest) -> Resources = {
    Resources.Builder().setVersion(it.version).apply(fn).build()
}

/**
 * Merges two [Resources] objects into a new one.
 *
 * The version of the resulting [Resources] object will be taken from the receiver (the left-hand
 * side of the +).
 *
 * If both [Resources] objects contain a resource with the same ID, the one from the [other]
 * (right-hand side) will be used in the final result.
 */
operator fun Resources.plus(other: Resources): Resources {
    val combinedImageMap = this.idToImageMapping + other.idToImageMapping
    return Resources.Builder()
        .setVersion(this.version)
        .apply {
            for ((id, resource) in combinedImageMap) {
                addIdToImageMapping(id, resource)
            }
        }
        .build()
}

// DeviceParameters extensions

fun MaterialScope.isLargeScreen() = deviceConfiguration.screenWidthDp >= 225

// Column extensions

fun column(builder: Column.Builder.() -> Unit) = Column.Builder().apply(builder).build()

fun row(builder: LayoutElementBuilders.Row.Builder.() -> Unit) =
    LayoutElementBuilders.Row.Builder().apply(builder).build()

fun box(builder: Box.Builder.() -> Unit) = Box.Builder().apply(builder).build()

// Image extensions

fun @receiver:DrawableRes Int.toImageResource(): ImageResource {
    return ImageResource.Builder()
        .setAndroidResourceByResId(
            ResourceBuilders.AndroidImageResourceByResId.Builder().setResourceId(this).build()
        )
        .build()
}

fun Bitmap.toImageResource(): ImageResource {
    val safeBitmap = this.copy(Bitmap.Config.RGB_565, false)

    val byteBuffer = ByteBuffer.allocate(safeBitmap.byteCount)
    safeBitmap.copyPixelsToBuffer(byteBuffer)
    val bytes: ByteArray = byteBuffer.array()

    return ImageResource.Builder()
        .setInlineResource(
            ResourceBuilders.InlineImageResource.Builder()
                .setData(bytes)
                .setWidthPx(this.width)
                .setHeightPx(this.height)
                .setFormat(ResourceBuilders.IMAGE_FORMAT_RGB_565)
                .build()
        )
        .build()
}