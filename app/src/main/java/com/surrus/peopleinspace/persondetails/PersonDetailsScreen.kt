@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.surrus.peopleinspace.persondetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.surrus.peopleinspace.ui.component.PeopleInSpaceGradientBackground
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PersonDetailsRoute(onBackClick: () -> Unit, viewModel: PersonDetailsViewModel = getViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PersonDetailsScreen(uiState, onBackClick)
}


@Composable
fun PersonDetailsScreen(uiState: PersonDetailsUiState, popBack: () -> Unit) {
    PeopleInSpaceGradientBackground {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(uiState.person?.name ?: "") },
                    navigationIcon = {
                        IconButton(onClick = { popBack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    //.consumedWindowInsets(it)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                uiState.person?.let { person ->
                    Spacer(modifier = Modifier.size(12.dp))

                    val imageUrl = person.personImageUrl ?: ""
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = person.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(240.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(24.dp))

                    val bio = person.personBio ?: ""
                    Text(
                        bio, style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


