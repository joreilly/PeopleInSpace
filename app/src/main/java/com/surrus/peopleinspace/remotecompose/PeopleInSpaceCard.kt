package com.surrus.peopleinspace.remotecompose

import android.annotation.SuppressLint
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteText
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
    RemoteBox(
        modifier = RemoteModifier.fillMaxSize().background(Color.DarkGray)
    ) {
        // TODO real content
        val count = rememberRemoteIntValue { 5 }
        val text = RemoteString("People in Space: ") + count.toRemoteString(1, 0)
        RemoteText(text, color = RemoteColor(Color.White))
    }
}

@Composable
@Preview(widthDp = 200, heightDp = 100)
fun PeopleInSpaceCardPreview() {
    RemotePreview {
        PeopleInSpaceCard()
    }
}
