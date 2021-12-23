package com.surrus.peopleinspace

import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.rotaryEventHandler(scrollState: ScrollableState): Modifier = composed {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaledVerticalScrollFactor =
        remember { ViewConfiguration.get(context).scaledVerticalScrollFactor }
    val view = LocalView.current
    SideEffect {
        // Activate rotary scrolling
        view.requestFocus()
    }

    pointerInteropFilter(RequestDisallowInterceptTouchEvent()) { event ->
        if (event.action != MotionEvent.ACTION_SCROLL ||
            !event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
        ) {
            false
        } else {
            val delta = -event.getAxisValue(MotionEventCompat.AXIS_SCROLL) *
                    scaledVerticalScrollFactor
            scope.launch {
                scrollState.scrollBy(delta)
            }
            true
        }
    }
}