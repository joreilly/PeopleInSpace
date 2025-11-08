package com.surrus.peopleinspace.remotecompose

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.capture.LocalRemoteComposeCreationState
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteText
import androidx.compose.remote.creation.compose.layout.remoteComponentHeight
import androidx.compose.remote.creation.compose.layout.remoteComponentWidth
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.background
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.RemoteString
import androidx.compose.remote.creation.compose.state.rememberRemoteIntValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.surrus.peopleinspace.remotecompose.util.RemotePreview

@SuppressLint("RestrictedApi")
@RemoteComposable
@Composable
fun PeopleInSpaceCard() {
    RemoteColumn(
        modifier = RemoteModifier.fillMaxSize().background(Color.DarkGray)
    ) {
        val state = LocalRemoteComposeCreationState.current
        // TODO real content
        val count = rememberRemoteIntValue { 5 }
        val width = remoteComponentWidth(state)
        val height = remoteComponentHeight(state)
        val text = RemoteString("People in Space: ") + count.toRemoteString(1, 0)
        RemoteText(text, color = RemoteColor(Color.White))

        val sizeText = RemoteString("Size: ") + width.toRemoteString(3, 0) + RemoteString("x") + height.toRemoteString(1, 0)
        RemoteText(sizeText, color = RemoteColor(Color.White))
    }
}

@Composable
@Preview(widthDp = 200, heightDp = 100)
fun PeopleInSpaceCardPreview() {
    RemotePreview {
        PeopleInSpaceCard()
    }
}
