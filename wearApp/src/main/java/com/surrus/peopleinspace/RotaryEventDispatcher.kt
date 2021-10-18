package com.surrus.peopleinspace

import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewConfigurationCompat
import kotlinx.coroutines.launch

val LocalRotaryEventDispatcher = staticCompositionLocalOf<RotaryEventDispatcher> {
    noLocalProvidedFor("LocalRotaryEventDispatcher")
}

/**
 * Dispatcher to link rotary event to [ScrollableState].
 * The instance should be set up by calling [RotaryEventHandlerSetup] function.
 */
class RotaryEventDispatcher(
    var scrollState: ScrollableState? = null
) {
    suspend fun onRotate(delta: Float): Float? =
        scrollState?.scrollBy(delta)
}

/**
 * Custom rotary event setup (Currently, Column / LazyColumn doesn't handle rotary event.)
 * Refer to https://developer.android.com/training/wearables/user-input/rotary-input
 */
@Composable
fun RotaryEventHandlerSetup(rotaryEventDispatcher: RotaryEventDispatcher) {
    val view = LocalView.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    view.requestFocus()
    view.setOnGenericMotionListener { _, event ->
        if (event?.action != MotionEvent.ACTION_SCROLL ||
            !event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
        ) {
            return@setOnGenericMotionListener false
        }

        val delta = -event.getAxisValue(MotionEventCompat.AXIS_SCROLL) *
            ViewConfigurationCompat.getScaledVerticalScrollFactor(
                ViewConfiguration.get(context), context
            )
        scope.launch {
            rotaryEventDispatcher.onRotate(delta)
        }
        true
    }
}

/**
 * Register a [ScrollableState] to [LocalRotaryEventDispatcher]
 */
@Composable
fun RotaryEventState(scrollState: ScrollableState?) {
    val dispatcher = LocalRotaryEventDispatcher.current
    SideEffect {
        dispatcher.scrollState = scrollState
    }
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}