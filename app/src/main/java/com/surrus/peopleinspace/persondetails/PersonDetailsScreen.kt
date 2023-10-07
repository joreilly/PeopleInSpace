@file:OptIn(ExperimentalMaterial3Api::class)

package com.surrus.peopleinspace.persondetails

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.surrus.peopleinspace.ui.PurpleGray50
import com.surrus.peopleinspace.ui.component.PeopleInSpaceGradientBackground
import org.koin.androidx.compose.getViewModel

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
                PersonDetailsTopAppBar(personName = uiState.person?.name ?: "", popBack = popBack)
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            PersonDetailsContent(uiState, innerPadding)
        }
    }
}

@Composable
fun PersonDetailsTopAppBar(personName: String, popBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(personName) },
        navigationIcon = {
            IconButton(onClick = { popBack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun PersonDetailsContent(uiState: PersonDetailsUiState, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        uiState.person?.let { person ->
            Spacer(modifier = Modifier.size(12.dp))
            PersonImage(person.personImageUrl, person.name)
            Spacer(modifier = Modifier.size(24.dp))
            PersonBio(person.personBio)
        } ?: run {
            Log.e("PersonDetailsContent", "Something went wrong!")
        }
    }
}

@Composable
fun PersonImage(imageUrl: String?, name: String) {
    imageUrl?.let {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(color = PurpleGray50)
        )
    } ?: run {
        Log.e("PersonImage", "Something went wrong!")
    }
}

@Composable
fun PersonBio(bio: String?) {
    bio?.let {
        Text(
            bio,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    } ?: run {
        Log.e("PersonBio", "Something went wrong!")
    }
}