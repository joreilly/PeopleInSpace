package com.surrus.peopleinspace.personlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.ui.PersonProvider
import com.surrus.peopleinspace.util.LoadingContent
import org.koin.androidx.compose.getViewModel

const val PersonListTag = "PersonList"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PersonListScreen(
    paddingValues: PaddingValues = PaddingValues(),
    personSelected: (person: Assignment) -> Unit,
    viewModel: PersonListViewModel = getViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("People In Space") })
        }
    ) { contentPadding ->

        LoadingContent(
            loading = uiState.isLoading,
            empty = uiState.items.isEmpty() && !uiState.isLoading,
            emptyContent = {},
            onRefresh = viewModel::refresh
        ) {
            LazyColumn(contentPadding = paddingValues, modifier = Modifier.testTag(PersonListTag).fillMaxSize()) {
                items(uiState.items) { person ->
                    PersonView(person, personSelected)
                }
            }
        }
    }
}

@Composable
fun PersonView(person: Assignment, personSelected: (person: Assignment) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { personSelected(person) })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val personImageUrl = person.personImageUrl ?: ""
        if (personImageUrl.isNotEmpty()) {
            AsyncImage(
                model = person.personImageUrl,
                contentDescription = person.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(60.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(60.dp))
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(text = person.name, style = TextStyle(fontSize = 20.sp))
            Text(text = person.craft, style = TextStyle(color = Color.DarkGray, fontSize = 14.sp))
        }
    }
}

@Preview
@Composable
fun PersonViewPreview(@PreviewParameter(PersonProvider::class) person: Assignment) {
    MaterialTheme {
        PersonView(person, personSelected = {})
    }
}