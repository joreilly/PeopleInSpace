/*
 * Copyright 2022 The Android Open Source Project
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
package com.surrus.peopleinspace.util

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import com.surrus.peopleinspace.BuildConfig
import com.surrus.peopleinspace.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.util.concurrent.TimeUnit

/**
 * Simple Jank log printer.
 */
class JankPrinter {
    private var stateHolder: PerformanceMetricsState.MetricsStateHolder? = null
    private lateinit var jankStats: JankStats
    private var nonJank = 0

    private fun Long.nanosToMillis() = "${TimeUnit.NANOSECONDS.toMillis(this)}ms"

    fun installJankStats(activity: Activity) {
        val contentView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
            .getChildAt(0) as ComposeView

        if (!BuildConfig.DEBUG) {
            stateHolder = PerformanceMetricsState.getForHierarchy(contentView).apply {
                state?.addState("Activity", activity.javaClass.simpleName)
                state?.addState("route", Screen.PersonList.route)
            }

            jankStats = JankStats.createAndTrack(
                activity.window,
                Dispatchers.Default.asExecutor(),
            ) {
                if (it.isJank) {
                    val route =
                        it.states.find { state -> state.stateName == "route" }?.state.orEmpty()
                    val duration = it.frameDurationUiNanos.nanosToMillis()
                    Log.w("Jank", "Jank $duration route:$route non:$nonJank")
                    nonJank = 0
                } else {
                    nonJank++
                }
            }.apply {
                // 3x isn't very noticeable for a few frames and settles down after the app has
                // been optimised.
                jankHeuristicMultiplier = 3f
            }
        }
    }

    fun setRouteState(route: String?) {
        stateHolder?.state?.let {
            if (route != null) {
                it.addState("route", route)
            } else {
                it.removeState("route")
            }
        }
    }
}
