@file:SuppressLint("RestrictedApi")

package dev.johnoreilly.peopleinspace.peopleinspace.remotecompose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.remote.tooling.preview.RemotePreview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.MapResult
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.fetchMapBitmapInRange
import dev.johnoreilly.peopleinspace.peopleinspace.remotecompose.util.filterRecent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RemoteComposeTestActivity : ComponentActivity(), KoinComponent {
    private val repository: PeopleInSpaceRepositoryInterface by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent { PeopleInSpaceDebugView() }
    }

    @Composable
    fun PeopleInSpaceDebugView() {
        var mapResult by remember { mutableStateOf<MapResult?>(null) }

        val windowInfo = LocalWindowInfo.current
        val sizeDp = DpSize(windowInfo.containerDpSize.width, 300.dp)
        val sizePx = with(LocalDensity.current) { sizeDp.toSize() }

        LaunchedEffect(Unit) {
            mapResult = fetchMapBitmapInRange(
                filterRecent(repository.fetchISSFuturePosition()),
                this@RemoteComposeTestActivity,
                size = sizePx,
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (mapResult != null) {
                Box(
                    modifier = Modifier.size(sizeDp)
                ) {
                    ShowRCPreview(mapResult!!)
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }

    @Composable
    private fun ShowRCPreview(mapResult: MapResult) {
        RemotePreview { PeopleInSpaceCard(mapResult) }
    }
}
