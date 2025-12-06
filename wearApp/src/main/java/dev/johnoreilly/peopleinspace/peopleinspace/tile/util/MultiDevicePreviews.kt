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

import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices

@Preview(device = WearDevices.SMALL_ROUND, name = "Small Round")
@Preview(device = WearDevices.LARGE_ROUND, name = "Large Round")
internal annotation class MultiRoundDevicesPreviews

@Preview(device = WearDevices.SMALL_ROUND, fontScale = 0.94f, name = "Small Round 0.94f")
@Preview(device = WearDevices.SMALL_ROUND, fontScale = 1.00f, name = "Small Round 1.00f")
@Preview(device = WearDevices.SMALL_ROUND, fontScale = 1.24f, name = "Small Round 1.24f")
@Preview(device = WearDevices.LARGE_ROUND, fontScale = 0.94f, name = "Large Round 0.94f")
@Preview(device = WearDevices.LARGE_ROUND, fontScale = 1.00f, name = "Large Round 1.00f")
@Preview(device = WearDevices.LARGE_ROUND, fontScale = 1.24f, name = "Large Round 1.24f")
internal annotation class MultiRoundDevicesWithFontScalePreviews