package com.surrus.peopleinspace.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.compose.frames.ModelList
import androidx.lifecycle.Observer
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutPadding
import androidx.ui.livedata.observeAsState
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.surrus.common.remote.Assignment
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val peopleInSpaceViewModel: PeopleInSpaceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            mainLayout(peopleInSpaceViewModel.peopleInSpace.observeAsState())
        }
    }
}

@Composable
fun mainLayout(peopleState: State<List<Assignment>?>) {
    MaterialTheme {
        Column {
            peopleState.value?.forEach { person ->
                Row(person)
            }
        }
    }
}


@Composable
fun Row(person: Assignment) {
    Text(
        text = "${person.name} (${person.craft})",
        modifier = LayoutPadding(16.dp)
    )
}



@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Row(Assignment("ISS", "John O'Reilly"))
    }
}