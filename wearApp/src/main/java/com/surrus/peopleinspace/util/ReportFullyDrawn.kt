package com.surrus.peopleinspace.util

import android.app.Activity
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.doOnPreDraw

// From https://github.com/androidx/androidx/blob/42d58ade87b9338c563ee8f182057a7da93f5c78/compose/integration-tests/macrobenchmark-target/src/main/java/androidx/compose/integration/macrobenchmark/target/FullyDrawnStartupActivity.kt
@Composable
fun ReportFullyDrawn() {
    val localView: View = LocalView.current
    SideEffect {
        val activity = localView.context as? Activity
        if (activity != null) {
            localView.doOnPreDraw {
                activity.reportFullyDrawn()
            }
        }
    }
}