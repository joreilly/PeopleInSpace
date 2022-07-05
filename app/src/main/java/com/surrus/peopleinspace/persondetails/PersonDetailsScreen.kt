package com.surrus.peopleinspace.persondetails

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.surrus.peopleinspace.personlist.PersonListViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLifecycleComposeApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PersonDetailsScreen(personName: String, popBack: () -> Unit) {
    val viewModel = getViewModel<PersonDetailsViewModel>(
        parameters = { parametersOf(personName) }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(personName) },
                navigationIcon = {
                    IconButton(onClick = { popBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiState.person?.let { person ->
                Text(person.name, style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.size(12.dp))

                val imageUrl = person.personImageUrl ?: ""
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = person.personImageUrl,
                        contentDescription = person.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(240.dp)
                    )
                }
                Spacer(modifier = Modifier.size(24.dp))

                val bio = person.personBio ?: ""
                Text(bio, style = MaterialTheme.typography.body1)
            }
        }
    }
}


