@file:OptIn(ExperimentalMaterial3Api::class)

package com.surrus.peopleinspace.personlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.ui.PeopleInSpaceTopAppBar
import com.surrus.peopleinspace.ui.PersonProvider
import com.surrus.peopleinspace.util.LoadingContent
import org.koin.androidx.compose.getViewModel
import com.surrus.peopleinspace.R
import com.surrus.peopleinspace.ui.component.PeopleInSpaceGradientBackground

const val PersonListTag = "PersonList"


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PersonListRoute(
    navigateToPerson: (String) -> Unit,
    viewModel: PersonListViewModel = getViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PersonListScreen(uiState, navigateToPerson)

}

@Composable
fun PersonListScreen(
    uiState: PersonListUiState,
    navigateToPerson: (String) -> Unit
) {
    PeopleInSpaceGradientBackground {
        Scaffold(
            topBar = {
                PeopleInSpaceTopAppBar(
                    titleRes = R.string.people_in_space,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->

            LoadingContent(
                loading = uiState.isLoading,
                empty = uiState.items.isEmpty() && !uiState.isLoading,
                emptyContent = {},
                onRefresh = {} //viewModel::refresh
            ) {
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier.testTag(PersonListTag).fillMaxSize()
                ) {
                    items(uiState.items) { person ->
                        PersonView(person, navigateToPerson)
                    }
                }
            }
        }
    }
}

@Composable
fun PersonView(person: Assignment, personSelected: (person: String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { personSelected(person.name) })
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