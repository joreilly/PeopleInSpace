package com.surrus.peopleinspace

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewConfigurationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun processEvent(context: Context, scope: CoroutineScope, event: MotionEvent, scrollState: ScrollableState): Boolean {
    if (event.action != MotionEvent.ACTION_SCROLL ||
        !event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
    ) {
        return false
    }

    val delta = -event.getAxisValue(MotionEventCompat.AXIS_SCROLL) *
            ViewConfigurationCompat.getScaledVerticalScrollFactor(
                ViewConfiguration.get(context), context
            )
    scope.launch {
        scrollState.scrollBy(delta)
    }
    return true
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.scrollHandler(scrollState: ScrollableState): Modifier = composed {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    this.pointerInteropFilter(RequestDisallowInterceptTouchEvent()) { event ->
        println(event)
        processEvent(context, scope, event, scrollState)
    }
}