package com.surrus.peopleinspace.remotecompose.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.remote.creation.compose.capture.CreationDisplayInfo
import androidx.compose.remote.creation.compose.capture.RemoteComposeCapture
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.profile.Profile
import androidx.compose.remote.creation.profile.RcPlatformProfiles
import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.ByteString
import okio.ByteString.Companion.toByteString

class RemoteComposeRecorder(private val context: Context) {
    @SuppressLint("RestrictedApi")
    suspend fun record(
        profile: Profile = RcPlatformProfiles.ANDROIDX,
        content: @RemoteComposable @Composable () -> Unit
    ): ByteString =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val connection = CreationDisplayInfo()
                RemoteComposeCapture(
                    context = context,
                    creationDisplayInfo = connection,
                    immediateCapture = true,
                    onPaint = { view, writer ->
                        val rcDocBytes = writer.encodeToByteArray().toByteString()
                        if (continuation.isActive) {
                            continuation.resume(
                                rcDocBytes, { _, _, _ ->
                                    println("Cancelled during execution")
                                }
                            )
                        }
                        true
                    },
                    onCaptureReady = @Composable {},
                    profile = profile,
                    content = content,
                )
                continuation.invokeOnCancellation {
                    println("Cancellation")
                }
            }
        }
}