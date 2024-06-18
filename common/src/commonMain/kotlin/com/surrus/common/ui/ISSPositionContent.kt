package com.surrus.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.surrus.common.viewmodel.ISSPositionViewModel

@Composable
fun ISSPositionContent(viewModel: ISSPositionViewModel) {
    val position by viewModel.position.collectAsStateWithLifecycle()

    Column {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Latitude = ${position.latitude}")
            Text(text = "Longitude = ${position.longitude}")
        }
        Spacer(Modifier.height(16.dp))
        ISSMapView(Modifier.fillMaxHeight().fillMaxWidth(), viewModel)
    }
}
