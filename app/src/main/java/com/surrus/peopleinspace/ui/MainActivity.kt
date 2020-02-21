package com.surrus.peopleinspace.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.surrus.common.remote.Assignment
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val peopleInSpaceViewModel: PeopleInSpaceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            mainLayout(peopleInSpaceViewModel)
        }
    }
}

@Composable
fun mainLayout(peopleInSpaceViewModel: PeopleInSpaceViewModel) {
    MaterialTheme {
        val people = observe(peopleInSpaceViewModel.peopleInSpace)
        Column {
            people?.forEach { person ->
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


// from https://medium.com/swlh/android-mvi-with-jetpack-compose-b0890f5156ac
// update: since dev05, version from that article was updated based
// on https://twitter.com/intelligibabble/status/1205318193960472576
@Composable fun <T> observe(data: LiveData<T>) : T? {
    val result = state<T?> { data.value }
    val observer = remember { Observer<T> { result.value = it } }

    onCommit(data) {
        data.observeForever(observer)
        onDispose { data.removeObserver(observer) }
    }

    return result.value
}


