@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)

package com.surrus.peopleinspace.personlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.surrus.common.remote.Assignment
import com.surrus.common.viewmodel.PersonListUiState
import com.surrus.common.viewmodel.PersonListViewModel
import com.surrus.peopleinspace.R
import com.surrus.peopleinspace.ui.PersonProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val PersonListTag = "PersonList"


@Composable
fun PersonListRoute(
navigateToPerson: (Assignment) -> Unit, ) {
    val viewModel: PersonListViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PersonListScreen(uiState, navigateToPerson, onRefresh = {
        viewModel.refresh()
    })

}

@Composable
fun PersonListScreen(
    uiState: PersonListUiState,
    navigateToPerson: (Assignment) -> Unit,
    onRefresh: () -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(500)
        onRefresh()
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.people_in_space)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.semantics { contentDescription = "PeopleInSpace" }
            )
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        when(uiState) {
            is PersonListUiState.Error -> {}
            is PersonListUiState.Loading -> {}
            is PersonListUiState.Success -> {
                Box(Modifier.pullRefresh(refreshState)) {
                    LazyColumn(
                        modifier = Modifier
                            .testTag(PersonListTag)
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .fillMaxSize()
                    ) {
                        if (!refreshing) {
                            items(uiState.result) { person ->
                                PersonView(person, navigateToPerson)
                            }
                        }
                    }

                    PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
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
            Text(text = person.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = person.craft, style = MaterialTheme.typography.bodyMedium)
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