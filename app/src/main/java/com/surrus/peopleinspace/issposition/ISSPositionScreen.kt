@file:OptIn(ExperimentalMaterial3Api::class)

package com.surrus.peopleinspace.issposition

import com.surrus.common.ui.ISSPositionContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import com.surrus.common.viewmodel.ISSPositionViewModel
import com.surrus.peopleinspace.R
import org.koin.androidx.compose.koinViewModel


@Composable
fun ISSPositionRoute(viewModel: ISSPositionViewModel = koinViewModel<ISSPositionViewModel>()) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.iss_position)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.semantics { contentDescription = "ISSPosition" }
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            ISSPositionContent(viewModel)
        }
    }
}

