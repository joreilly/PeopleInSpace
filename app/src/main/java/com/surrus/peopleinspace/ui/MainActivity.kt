package com.surrus.peopleinspace.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.compose.frames.ModelList
import androidx.lifecycle.Observer
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.surrus.common.remote.Assignment
import org.koin.android.viewmodel.ext.android.viewModel


@Model
class PeopleState(var peopleInSpace: ModelList<Assignment> = ModelList())

class MainActivity : AppCompatActivity() {
    private val peopleInSpaceViewModel: PeopleInSpaceViewModel by viewModel()
    private val peopleState = PeopleState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        peopleInSpaceViewModel.peopleInSpace.observe(this, Observer {
            peopleState.peopleInSpace.clear()
            peopleState.peopleInSpace.addAll(it)
        })

        setContent {
            mainLayout(peopleState)
        }
    }
}

@Composable
fun mainLayout(peopleState: PeopleState) {
    MaterialTheme {
        Column {
            peopleState.peopleInSpace.forEach { person ->
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