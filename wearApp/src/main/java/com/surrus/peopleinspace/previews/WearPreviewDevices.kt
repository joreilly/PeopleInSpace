/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.surrus.peopleinspace.previews;

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Large Round",
)
@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
)
@Preview(
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Square",
)
public annotation class WearPreviewDevices

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
)
public annotation class WearSmallRoundDevicePreview

@Preview(
    device = Devices.WEAR_OS_LARGE_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Large Round",
)
public annotation class WearLargeRoundDevicePreview

@Preview(
    device = Devices.WEAR_OS_SQUARE,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Square",
)
public annotation class WearSquareDevicePreview
