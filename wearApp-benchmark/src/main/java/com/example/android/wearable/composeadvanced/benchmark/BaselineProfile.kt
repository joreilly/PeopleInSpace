/*
 * Copyright 2021 The Android Open Source Project
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
package com.example.android.wearable.composeadvanced.benchmark

import android.graphics.Point
import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// This test generates a baseline profile rules file that can be added to the app to configure
// the classes and methods that are pre-compiled at installation time, rather than JIT'd at runtime.
// 1) Run this test on a device
// 2) Copy the generated file to your workspace - command is output as part of the test:
// `adb pull "/sdcard/Android/media/com.example.android.wearable.composeadvanced.benchmark/"
//           "additional_test_output/BaselineProfile_profile-baseline-prof-2022-03-25-16-58-49.txt"
//           .`
// 3) Add the rules as main/baseline-prof.txt
// Note that Compose libraries have profile rules already so the main benefit is to add any
// rules that are specific to classes and methods in your own app and library code.
@ExperimentalBaselineProfilesApi
@RunWith(AndroidJUnit4::class)
class BaselineProfile {

    @get:Rule
    val baselineRule = BaselineProfileRule()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    @Test
    fun profile() {
        baselineRule.collectBaselineProfile(
            packageName = PACKAGE_NAME,
            profileBlock = {
                startActivityAndWait()

                scrollDown()
                findAndClickResource("Person")
                scrollDown("")
                backWhenIdle()
            }
        )
    }

    private fun scrollDown(resourceName: String? = null) {
        // Scroll down to view remaining UI elements
        // Setting a gesture margin is important otherwise gesture nav is triggered.
        device.waitForIdle()
        val finder = if (resourceName != null)
            By.res(resourceName)
        else
            By.scrollable(true)
        val list = device.findObject(finder)
        list.setGestureMargin(device.displayWidth / 5)
        list.drag(Point(list.visibleCenter.x, list.visibleCenter.y / 2))
        device.waitForIdle()
    }

    private fun findAndClickText(text: String) {
        device.wait(Until.findObject(By.text(text)), 3000)
        device.findObject(By.text(text)).click()
        device.waitForIdle()
    }

    private fun findAndClickResource(resourceName: String) {
        device.wait(Until.findObject(By.res(resourceName)), 3000)
        device.findObject(By.res(resourceName)).click()
        device.waitForIdle()
    }

    private fun findAndClickDesc(desc: String) {
        device.wait(Until.findObject(By.desc(desc)), 3000)
        device.findObject(By.desc(desc)).click()
        device.waitForIdle()
    }

    private fun backWhenIdle() {
        device.waitForIdle()
        device.pressBack()
        device.waitForIdle()
    }

    companion object {
        private const val PACKAGE_NAME = "com.surrus.peopleinspace"
    }
}
